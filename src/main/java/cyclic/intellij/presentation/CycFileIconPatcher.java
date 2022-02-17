package cyclic.intellij.presentation;

import com.intellij.ide.FileIconPatcher;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.LayeredIcon;
import cyclic.intellij.CyclicFileType;
import cyclic.intellij.CyclicIcons;
import cyclic.intellij.psi.CycFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CycFileIconPatcher implements FileIconPatcher{
	
	public Icon patchIcon(Icon baseIcon, VirtualFile file, int flags, @Nullable Project project){
		if(file.getFileType() != CyclicFileType.FILE_TYPE || project == null)
			return baseIcon;
		
		PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
		if(psiFile instanceof CycFile){
			CycFile cycFile = (CycFile)psiFile;
			if(cycFile.getTypeDef().isPresent())
				return new LayeredIcon(cycFile.getTypeDef().get().getIcon(flags), CyclicIcons.CYCLIC_DECORATION);
		}
		
		return baseIcon;
	}
}
