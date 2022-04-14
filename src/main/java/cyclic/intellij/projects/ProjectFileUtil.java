package cyclic.intellij.projects;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.patterns.VirtualFilePattern;

public class ProjectFileUtil{
	
	public static final String PROJECT_YAML_EXTENSION = ".cyc.yaml";
	
	public static boolean isProjectFile(VirtualFile file){
		return file.getName().endsWith(PROJECT_YAML_EXTENSION);
	}
	
	public static VirtualFilePattern projectFilePattern(){
		return PlatformPatterns
				.virtualFile()
				.withName(StandardPatterns
						.string().endsWith(PROJECT_YAML_EXTENSION));
	}
}