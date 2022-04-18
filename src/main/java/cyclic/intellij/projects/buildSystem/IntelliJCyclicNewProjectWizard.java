package cyclic.intellij.projects.buildSystem;

import com.intellij.ide.projectWizard.NewProjectWizardCollector;
import com.intellij.ide.projectWizard.generators.IntelliJNewProjectWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.NewProjectWizardStep;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import cyclic.intellij.projects.BuildSystemCyclicNewProjectWizard;
import cyclic.intellij.projects.CyclicNewProjectStep;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class IntelliJCyclicNewProjectWizard implements BuildSystemCyclicNewProjectWizard{
	
	@NotNull
	public String getName(){
		return "IntelliJ";
	}
	
	@NotNull
	public NewProjectWizardStep createStep(@NotNull CyclicNewProjectStep step){
		return new IntelliJCyclicNewProjectWizardStep(step);
	}
	
	public boolean isEnabled(@NotNull WizardContext context){
		return true;
	}
	
	public int getOrdinal(){
		return 1;
	}
	
	public static class IntelliJCyclicNewProjectWizardStep extends IntelliJNewProjectWizardStep<CyclicNewProjectStep>{
		
		public IntelliJCyclicNewProjectWizardStep(@NotNull CyclicNewProjectStep parent){
			super(parent);
		}
		
		public void setupProject(@NotNull Project project){
			CyclicModuleBuilder builder = new CyclicModuleBuilder();
			Path moduleFile = Paths.get(this.getModuleFileLocation(), getModuleName() + ".iml");
			builder.setName(getModuleName());
			builder.setModuleFilePath(FileUtil.toSystemDependentName(moduleFile.toString()));
			builder.setContentEntryPath(FileUtil.toSystemDependentName(getContentRoot()));
			
			Utils.setProjectOrModuleSdk(project, getParent(), builder, Optional.ofNullable(getSdk()));
			
			addDefaultCode(builder, project);
			
			builder.commit(project);
			NewProjectWizardCollector.BuildSystem.INSTANCE.logSdkFinished(getParent(), getSdk());
		}
		
		public void addDefaultCode(CyclicModuleBuilder builder, Project project){
			if(getAddSampleCode())
				builder.openWhenProjectCreated = Utils.tryAddSampleCode(project, getContentRoot() + "/src");
		}
	}
}