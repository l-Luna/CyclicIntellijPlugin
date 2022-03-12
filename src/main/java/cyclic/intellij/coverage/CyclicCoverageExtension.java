package cyclic.intellij.coverage;

import com.intellij.coverage.*;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.ast.types.CycType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Set;

public class CyclicCoverageExtension extends JavaCoverageEngineExtension{
	
	public boolean isApplicableTo(@Nullable RunConfigurationBase conf){
		return true;
	}
	
	public boolean keepCoverageInfoForClassWithoutSource(CoverageSuitesBundle bundle, File classFile){
		return false;
	}
	
	public PackageAnnotator.@Nullable ClassCoverageInfo getSummaryCoverageInfo(JavaCoverageAnnotator annotator, PsiNamedElement element){
		if(element instanceof CycType)
			return annotator.getClassCoverageInfo(((CycType)element).fullyQualifiedName());
		return null;
	}
	
	public boolean collectOutputFiles(@NotNull PsiFile srcFile,
	                                  @Nullable VirtualFile output,
	                                  @Nullable VirtualFile testOutput,
	                                  @NotNull CoverageSuitesBundle suite,
	                                  @NotNull Set<File> classFiles){
		if(srcFile instanceof CycFile){
			var idx = ProjectRootManager.getInstance(srcFile.getProject()).getFileIndex();
			if(idx.isInLibraryClasses(srcFile.getVirtualFile()) || idx.isInLibrarySource(srcFile.getVirtualFile()))
				return false;
			return ReadAction.compute(() -> {
				var def = ((CycFile)srcFile).getTypeDef();
				if(def.isEmpty())
					return false;
				var module = ModuleUtilCore.findModuleForPsiElement(srcFile);
				if(module == null)
					return false;
				boolean isTest = idx.isInTestSourceContent(srcFile.getVirtualFile());
				var roots =
						JavaCoverageClassesEnumerator.getRoots(CoverageDataManager.getInstance(suite.getProject()), module, isTest);
				for(VirtualFile root : roots){
					var name = def.get().fullyQualifiedName().replace(".", "/");
					var path = root.findFileByRelativePath(name + ".class");
					if(path != null)
						classFiles.add(new File(path.getPath()));
				}
				return true;
			});
		}
		return false;
	}
}