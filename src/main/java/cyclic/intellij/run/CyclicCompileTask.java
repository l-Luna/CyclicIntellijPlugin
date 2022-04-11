package cyclic.intellij.run;

import com.intellij.compiler.ModuleSourceSet;
import com.intellij.compiler.server.BuildManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileTask;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.model.facet.WorkspaceSdk;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.intellij.openapi.util.io.FileUtil.toSystemDependentName;
import static cyclic.intellij.projects.CyclicProjectYamlFileIconPatcher.PROJECT_YAML_EXTENSION;
import static org.jetbrains.jps.model.java.JavaSourceRootType.SOURCE;
import static org.jetbrains.jps.model.java.JavaSourceRootType.TEST_SOURCE;

public class CyclicCompileTask implements CompileTask{
	
	// We're *supposed* to use the JPS module system to handle building.
	// but there's literally no point to it,
	// we still have to manage the compiler process manually
	// except that way we have to re-serialize stuff twice
	
	public boolean execute(CompileContext context){
		var project = context.getProject();
		for(ModuleSourceSet set : context.getCompileScope().getAffectedSourceSets()){
			var module = set.getModule();
			var roots = ModuleRootManager.getInstance(module).getSourceRoots(
					set.getType() == ModuleSourceSet.Type.TEST ? TEST_SOURCE : SOURCE);
			// we can only actually handle a single project file or source root per module
			// but first, check that it actually has any cyclic code
			AtomicBoolean foundCyclic = new AtomicBoolean(false);
			AtomicReference<VirtualFile> projectFile = new AtomicReference<>(null);
			for(VirtualFile root : roots){
				if(foundCyclic.get())
					break;
				if(projectFile.get() == null)
					for(VirtualFile sibling : root.getParent().getChildren())
						if(sibling.getName().endsWith(PROJECT_YAML_EXTENSION))
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
				context.addMessage(CompilerMessageCategory.ERROR,
						CyclicBundle.message("compiler.error.roots"),
						null, -1, -1);
				return false;
			}
			if(!foundCyclic.get() || roots.size() == 0)
				return true;
			
			String compilerPath = WorkspaceSdk.getFor(project).compilerPath;
			if(compilerPath == null || compilerPath.isBlank()){
				context.addMessage(CompilerMessageCategory.ERROR,
						CyclicBundle.message("compiler.error.missing.cyclic"),
						null, -1, -1);
				return false;
			}
			
			var sdkInfo = BuildManager.getJavacRuntimeSdk(project);
			var sdk = sdkInfo.first;
			var sdkType = sdk.getSdkType();
			if(!(sdkType instanceof JavaSdkType)){
				context.addMessage(CompilerMessageCategory.ERROR,
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
				context.addMessage(CompilerMessageCategory.INFORMATION,
						CyclicBundle.message("compiler.notice.foundProject"),
						null, -1, -1);
				appendParam(cmdLine, "-p");
				appendParam(cmdLine, toSystemDependentName(projectFile.get().getPath()));
			}else{
				context.addMessage(CompilerMessageCategory.INFORMATION,
						CyclicBundle.message("compiler.notice.moduleRoots"),
						null, -1, -1);
				appendParam(cmdLine, toSystemDependentName(roots.get(0).getPath()));
				appendParam(cmdLine, toSystemDependentName(context.getModuleOutputDirectory(module).getPath()));
			}
			
			try{
				var compilerProcess = new ProcessBuilder(cmdLine).start();
				var code = compilerProcess.waitFor();
				var bytes = compilerProcess.getErrorStream().readAllBytes();
				var log = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes)).toString();
				
				if(code != 0){
					context.addMessage(CompilerMessageCategory.ERROR,
							CyclicBundle.message("compiler.error.generic", log),
							null, -1, -1);
					return false;
				}else if(!log.isBlank())
					context.addMessage(CompilerMessageCategory.WARNING,
							CyclicBundle.message("compiler.warning.generic", log),
							null, -1, -1);
			}catch(IOException | InterruptedException e){
				context.addMessage(CompilerMessageCategory.ERROR,
						CyclicBundle.message("compiler.error.process", e),
						null, -1, -1);
			}
		}
		return true;
	}
	
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