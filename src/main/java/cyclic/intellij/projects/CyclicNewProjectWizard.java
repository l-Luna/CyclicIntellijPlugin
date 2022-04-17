package cyclic.intellij.projects;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.LanguageNewProjectWizard;
import com.intellij.ide.wizard.NewProjectWizardLanguageStep;
import com.intellij.ide.wizard.NewProjectWizardStep;
import org.jetbrains.annotations.NotNull;

public class CyclicNewProjectWizard implements LanguageNewProjectWizard{
	
	@NotNull
	public String getName(){
		return "Cyclic";
	}
	
	@NotNull
	public NewProjectWizardStep createStep(@NotNull NewProjectWizardLanguageStep parent){
		return new CyclicNewProjectStep(parent);
	}
	
	public boolean isEnabled(@NotNull WizardContext context){
		return true;
	}
	
	public int getOrdinal(){
		// Groovy is 200, Scala is 201, we want to be just after
		return 202;
	}
}