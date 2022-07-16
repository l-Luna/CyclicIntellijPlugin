package cyclic.intellij.run;

import com.intellij.compiler.server.BuildManager;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.model.facet.WorkspaceSdk;
import cyclic.intellij.projectFiles.ProjectFileUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.intellij.openapi.util.io.FileUtil.toSystemDependentName;
import static org.jetbrains.jps.model.java.JavaSourceRootType.SOURCE;

public class CompilerInvoker{
	
	public static boolean invoke(Project project, Reporter reporter, String... args){
		Module[] modules = ModuleManager.getInstance(project).getModules();
		return invoke(project, List.of(modules), reporter, args);
	}
	
	public static boolean invoke(Project project, List<Module> modules, Reporter reporter, String... args){
		for(Module module : modules){
			var roots = ModuleRootManager.getInstance(module).getSourceRoots(SOURCE);
			// we can only actually handle a single project file or source root per module
			// but first, check that it actually has any cyclic code
			AtomicBoolean foundCyclic = new AtomicBoolean(false);
			AtomicReference<VirtualFile> projectFile = new AtomicReference<>(null);
			for(VirtualFile root : roots){
				if(foundCyclic.get())
					break;
				if(projectFile.get() == null)
					for(VirtualFile sibling : root.getParent().getChildren())
						if(ProjectFileUtil.isProjectFile(sibling))
							projectFile.set(sibling);
				VfsUtil.iterateChildrenRecursively(root, x -> true, vf -> {
					if(vf.getName().endsWith(".cyc")){
						foundCyclic.set(true);
						return false;
					}
					return true;
				});
			}
			if(foundCyclic.get() && roots.size() > 1){
				reporter.addMessage(CompilerMessageCategory.ERROR,
						CyclicBundle.message("compiler.error.roots"),
						null, -1, -1);
				return false;
			}
			if(!foundCyclic.get() || roots.size() == 0)
				return true;
			
			String compilerPath = WorkspaceSdk.getFor(project).compilerPath;
			if(compilerPath == null || compilerPath.isBlank()){
				reporter.addMessage(CompilerMessageCategory.ERROR,
						CyclicBundle.message("compiler.error.missing.cyclic"),
						null, -1, -1);
				return false;
			}
			
			var sdkInfo = BuildManager.getJavacRuntimeSdk(project);
			var sdk = sdkInfo.first;
			var sdkType = sdk.getSdkType();
			if(!(sdkType instanceof JavaSdkType)){
				reporter.addMessage(CompilerMessageCategory.ERROR,
						CyclicBundle.message("compiler.error.missing.java"),
						null, -1, -1);
				return false;
			}
			String javaHome = sdk.getHomePath();
			// don't question it
			// even though CompilerManagerImpl::compilerJavaCode does
			
			List<String> cmdLine = new ArrayList<>();
			
			// either
			//   java --enable-preview -jar <compiler> <root path> <output path>
			//   java --enable-preview -jar <compiler> -p <project path>
			appendParam(cmdLine, getVMExecutablePath(javaHome));
			appendParam(cmdLine, "--enable-preview");
			appendParam(cmdLine, "-jar");
			appendParam(cmdLine, toSystemDependentName(compilerPath));
			
			if(projectFile.get() != null){
				reporter.addMessage(CompilerMessageCategory.INFORMATION,
						CyclicBundle.message("compiler.notice.foundProject"),
						null, -1, -1);
				appendParam(cmdLine, "-p");
				appendParam(cmdLine, toSystemDependentName(projectFile.get().getPath()));
			}else{
				reporter.addMessage(CompilerMessageCategory.INFORMATION,
						CyclicBundle.message("compiler.notice.moduleRoots"),
						null, -1, -1);
				appendParam(cmdLine, toSystemDependentName(roots.get(0).getPath()));
				appendParam(cmdLine, toSystemDependentName(CompilerPaths.getModuleOutputPath(module, false)));
			}
			
			for(String arg : args)
				appendParam(cmdLine, arg);
			
			System.out.println(cmdLine);
			
			try{
				var compilerProcess = new ProcessBuilder(cmdLine).start();
				var code = compilerProcess.waitFor();
				var bytes = compilerProcess.getErrorStream().readAllBytes();
				var log = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes)).toString();
				
				if(code != 0){
					reporter.addMessage(CompilerMessageCategory.ERROR,
							CyclicBundle.message("compiler.error.generic", log),
							null, -1, -1);
					return false;
				}else if(!log.isBlank())
					reporter.addMessage(CompilerMessageCategory.WARNING,
							CyclicBundle.message("compiler.warning.generic", log),
							null, -1, -1);
			}catch(IOException | InterruptedException e){
				reporter.addMessage(CompilerMessageCategory.ERROR,
						CyclicBundle.message("compiler.error.process", e),
						null, -1, -1);
				return false;
			}
		}
		return true;
	}
	
	@FunctionalInterface
	public interface Reporter{
		void addMessage(CompilerMessageCategory cmc, String message, String url, int line, int column);
	}
	
	public static final Reporter NO_OP_REPORTER = (cmc, message, url, line, column) -> {};
	
	private static String getVMExecutablePath(String sdkHome){
		return sdkHome + "/bin/java";
	}
	
	private static void appendParam(List<String> cmdLine, String parameter){
		if(SystemInfo.isWindows)
			if(parameter.contains("\""))
				parameter = StringUtil.replace(parameter, "\"", "\\\"");
			else if(parameter.length() == 0)
				parameter = "\"\"";
		cmdLine.add(parameter);
	}
}