package cyclic.intellij.projects.buildSystem;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.NewProjectWizardStep;
import com.intellij.openapi.project.Project;
import cyclic.intellij.projects.BuildSystemCyclicNewProjectWizard;
import cyclic.intellij.projects.CyclicNewProjectStep;
import org.jetbrains.annotations.NotNull;

public class CyclicCompilerNewProjectWizard implements BuildSystemCyclicNewProjectWizard{
	
	@NotNull
	public String getName(){
		return "Cyclic Project";
	}
	
	@NotNull
	public NewProjectWizardStep createStep(@NotNull CyclicNewProjectStep step){
		return new CyclicCompilerNewProjectWizardStep(step);
	}
	
	public boolean isEnabled(@NotNull WizardContext context){
		return true;
	}
	
	public int getOrdinal(){
		return 1;
	}
	
	public static class CyclicCompilerNewProjectWizardStep extends IntelliJCyclicNewProjectWizard.IntelliJCyclicNewProjectWizardStep{
		
		public CyclicCompilerNewProjectWizardStep(@NotNull CyclicNewProjectStep parent){
			super(parent);
		}
		
		public void addDefaultCode(CyclicModuleBuilder builder, Project project){
			if(Utils.tryAddSampleProjectFile(project, getContentRoot()) == null)
				throw new IllegalStateException("Could not create project file");
			if(getAddSampleCode())
				builder.openWhenProjectCreated = Utils.tryAddSampleCode(project, getContentRoot() + "/src");
		}
	}
}