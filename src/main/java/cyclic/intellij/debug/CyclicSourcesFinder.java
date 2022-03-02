package cyclic.intellij.debug;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nullable;

public class CyclicSourcesFinder{
	
	public static @Nullable PsiFile findSourceFile(String relPath, Project project){
		// Like "java/lang/Thread.java"
		// also completely generic for JVM languages?
		var path = FileUtil.toSystemIndependentName(relPath);
		if(path.startsWith("java/") || path.startsWith("jdk/") || path.startsWith("sun/"))
			return null;
		var modules = ModuleManager.getInstance(project).getModules();
		for(Module module : modules){
			var roots = ModuleRootManager.getInstance(module).getSourceRoots();
			for(VirtualFile root : roots){
				var file = root.findFileByRelativePath(path);
				if(file != null && file.getName().endsWith(".cyc")){
					var psiFile = PsiManager.getInstance(project).findFile(file);
					if(psiFile != null)
						return psiFile;
				}
			}
		}
		return null;
	}
}