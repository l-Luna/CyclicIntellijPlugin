package cyclic.intellij.projects;

import com.intellij.ide.FileIconPatcher;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import cyclic.intellij.CyclicIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CyclicProjectYamlFileIconPatcher implements FileIconPatcher, DumbAware{
	
	public static final String PROJECT_YAML_EXTENSION = ".cyc.yaml";
	
	public Icon patchIcon(Icon baseIcon, VirtualFile file, int flags, @Nullable Project project){
		if(file.getName().endsWith(PROJECT_YAML_EXTENSION))
			return CyclicIcons.CYCLIC_FILE; // TODO: better icon
		return baseIcon;
	}
}