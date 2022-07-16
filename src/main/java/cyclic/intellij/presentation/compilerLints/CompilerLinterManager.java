package cyclic.intellij.presentation.compilerLints;

import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiModificationTracker;
import cyclic.intellij.run.CompilerInvoker;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Supplier;

public class CompilerLinterManager{

	private static long lastModificationCount;
	private static List<CompileProblem> lastProblems;
	
	public static Supplier<List<CompileProblem>> getProblems(Project project){
		PsiModificationTracker tracker = PsiModificationTracker.getInstance(project);
		
		if(tracker.getModificationCount() == lastModificationCount)
			return () -> lastProblems;
		
		return () -> {
			long currentModificationCount = tracker.getModificationCount();
			try{
				File temp = File.createTempFile(String.valueOf(project.getName().hashCode()), ".diag.yaml");
				boolean result = CompilerInvoker.invoke(project, CompilerInvoker.NO_OP_REPORTER, "--noOutput", "--diagnostics",  temp.getAbsolutePath());
				List<CompileProblem> newProblems;
				if(result){
					String out = Files.readString(temp.toPath());
					temp.delete();
					Constructor constructor = new Constructor();
					constructor.getPropertyUtils().setBeanAccess(BeanAccess.FIELD);
					constructor.addTypeDescription(new TypeDescription(CompileProblem.class, new Tag("tag:yaml.org,2002:cyclic.lang.compiler.problems.Problem$Warning")));
					constructor.addTypeDescription(new TypeDescription(CompileProblem[].class, new Tag("tag:yaml.org,2002:[Lcyclic.intellij.presentation.compilerLints.CompileProblem;")));
					CompileProblem[] problems = new Yaml(constructor).loadAs(out, CompileProblem[].class);
					if(problems != null && problems.length > 0)
						newProblems = List.of(problems);
					else
						newProblems = List.of();
				}else throw new RuntimeException("Could not run compiler for compiler linting");
				lastModificationCount = currentModificationCount;
				lastProblems = newProblems;
				return newProblems;
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		};
	}
}