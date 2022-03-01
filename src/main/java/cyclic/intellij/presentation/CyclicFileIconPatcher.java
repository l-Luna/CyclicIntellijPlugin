package cyclic.intellij.presentation;

import com.intellij.ide.FileIconPatcher;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import cyclic.intellij.CyclicFileType;
import cyclic.intellij.psi.CycFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CyclicFileIconPatcher implements FileIconPatcher{
	
	public Icon patchIcon(Icon baseIcon, VirtualFile file, int flags, @Nullable Project project){
		if(file.getFileType() != CyclicFileType.FILE_TYPE || project == null)
			return baseIcon;
		
		PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
		if(psiFile instanceof CycFile){
			CycFile cycFile = (CycFile)psiFile;
			if(cycFile.getTypeDef().isPresent())
				return cycFile.getTypeDef().get().getIcon(flags);
		}
		
		return baseIcon;
	}
}
