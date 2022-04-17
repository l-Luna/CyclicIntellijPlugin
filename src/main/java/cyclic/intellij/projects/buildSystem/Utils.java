package cyclic.intellij.projects.buildSystem;

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.wizard.AbstractNewProjectWizardStep;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public final class Utils{
	
	private static final Object REQUESTER = new Object();
	
	public static void setProjectOrModuleSdk(Project project,
	                                         AbstractNewProjectWizardStep parent,
	                                         ModuleBuilder builder,
	                                         Optional<Sdk> sdk){
		var ctx = parent.getContext();
		if(ctx.isCreatingNewProject()){
			// New project
			ctx.setProjectJdk(sdk.orElse(null));
		}else{
			// New module, existing project
			var roots = ProjectRootManager.getInstance(project);
			Sdk projectSdk = roots.getProjectSdk();
			String sdkName = projectSdk != null ? projectSdk.getName() : null;
			boolean sameSDK = Objects.equals(sdkName, sdk.map(Sdk::getName).orElse(null));
			builder.setModuleJdk(sameSDK ? null : sdk.orElse(null));
		}
	}
	
	public static VirtualFile addTemplate(Project project, String path, String templateName, String fileName) throws IOException{
		var manager = FileTemplateManager.getInstance(project);
		var template = manager.getInternalTemplate(templateName);
		var source = template.getText();
		
		return WriteAction.compute(() -> {
			VirtualFile dir;
			try{
				dir = VfsUtil.createDirectoryIfMissing(path);
				assert dir != null;
			}catch(IOException e){
				throw new IllegalStateException("Could not create source directory", e);
			}
			VirtualFile file = dir.findOrCreateChildData(REQUESTER, fileName);
			VfsUtil.saveText(file, source);
			return file;
		});
	}
	
	public static VirtualFile tryAddTemplate(Project project, String path, String templateName, String fileName){
		try{
			return addTemplate(project, path, templateName, fileName);
		}catch(IOException e){
			return null;
		}
	}
	
	public static VirtualFile tryAddSampleCode(Project project, String path){
		return tryAddTemplate(project, path, "example-project-code.cyc", "Main.cyc");
	}
	
	public static VirtualFile tryAddSampleProjectFile(Project project, String path){
		return tryAddTemplate(project, path, "example-project.cyc.yaml", "project.cyc.yaml");
	}
}