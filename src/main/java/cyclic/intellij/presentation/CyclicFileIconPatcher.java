package cyclic.intellij.presentation;

import com.intellij.icons.AllIcons;
import com.intellij.ide.FileIconPatcher;
import com.intellij.lang.jvm.util.JvmMainMethodUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.LayeredIcon;
import com.intellij.util.PlatformIcons;
import cyclic.intellij.CyclicFileType;
import cyclic.intellij.CyclicIcons;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.CycType;
import cyclic.intellij.psi.types.JvmCyclicClass;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CyclicFileIconPatcher implements FileIconPatcher{
	
	public Icon patchIcon(Icon baseIcon, VirtualFile file, int flags, @Nullable Project project){
		if(file.getFileType() != CyclicFileType.FILE_TYPE || project == null)
			return baseIcon;
		
		PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
		if(psiFile instanceof CycFile){
			CycFile cycFile = (CycFile)psiFile;
			if(cycFile.getTypeDef().isPresent()){
				var type = cycFile.getTypeDef().get();
				var icon = new LayeredIcon(type.getIcon(flags), CyclicIcons.CYCLIC_DECORATION);
				if(JvmMainMethodUtil.hasMainMethodInHierarchy(JvmCyclicClass.of(type)))
					icon = new LayeredIcon(type.getIcon(flags), CyclicIcons.CYCLIC_DECORATION, AllIcons.Nodes.RunnableMark);
				return icon;
			}
		}
		
		return baseIcon;
	}
}
