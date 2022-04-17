package cyclic.intellij.projects;

import com.intellij.ide.wizard.NewProjectWizardMultiStepFactory;
import com.intellij.openapi.extensions.ExtensionPointName;

public interface BuildSystemCyclicNewProjectWizard extends NewProjectWizardMultiStepFactory<CyclicNewProjectStep>{
	
	ExtensionPointName<BuildSystemCyclicNewProjectWizard> EP_NAME
			= ExtensionPointName.create("cyclic.intellij.newProjectWizard.buildSystem");
}