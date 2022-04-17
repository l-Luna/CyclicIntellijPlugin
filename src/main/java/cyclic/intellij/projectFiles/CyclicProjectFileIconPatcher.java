package cyclic.intellij.projectFiles;

import com.intellij.ide.FileIconPatcher;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import cyclic.intellij.CyclicIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CyclicProjectFileIconPatcher implements FileIconPatcher, DumbAware{
	
	public Icon patchIcon(Icon baseIcon, VirtualFile file, int flags, @Nullable Project project){
		if(ProjectFileUtil.isProjectFile(file))
			return CyclicIcons.CYCLIC_FILE; // TODO: better icon
		return baseIcon;
	}
}