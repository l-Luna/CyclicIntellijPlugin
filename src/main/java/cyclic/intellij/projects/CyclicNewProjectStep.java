package cyclic.intellij.projects;

import com.intellij.ide.JavaUiBundle;
import com.intellij.ide.wizard.AbstractNewProjectWizardMultiStep;
import com.intellij.ide.wizard.BuildSystemNewProjectWizardData;
import com.intellij.ide.wizard.NewProjectWizardLanguageStep;
import com.intellij.openapi.observable.properties.GraphProperty;
import org.jetbrains.annotations.NotNull;

public class CyclicNewProjectStep
		extends AbstractNewProjectWizardMultiStep<CyclicNewProjectStep, BuildSystemCyclicNewProjectWizard>
		implements BuildSystemNewProjectWizardData{
	
	protected NewProjectWizardLanguageStep parent;
	
	public CyclicNewProjectStep(@NotNull NewProjectWizardLanguageStep parent){
		super(parent, BuildSystemCyclicNewProjectWizard.EP_NAME);
		this.parent = parent;
	}
	
	protected CyclicNewProjectStep getSelf(){
		return this;
	}
	
	@NotNull
	protected String getLabel(){
		return JavaUiBundle.message("label.project.wizard.new.project.build.system");
	}
	
	// just delegate to the parent
	
	@NotNull
	public String getLanguage(){
		return parent.getLanguage();
	}
	
	public void setLanguage(@NotNull String s){
		parent.setLanguage(s);
	}
	
	@NotNull
	public GraphProperty<String> getLanguageProperty(){
		return parent.getLanguageProperty();
	}
	
	@NotNull
	public String getName(){
		return parent.getName();
	}
	
	public void setName(@NotNull String s){
		parent.setName(s);
	}
	
	@NotNull
	public GraphProperty<String> getNameProperty(){
		return parent.getNameProperty();
	}
	
	@NotNull
	public String getPath(){
		return parent.getPath();
	}
	
	public void setPath(@NotNull String s){
		parent.setPath(s);
	}
	
	@NotNull
	public GraphProperty<String> getPathProperty(){
		return parent.getPathProperty();
	}
	
	@NotNull
	public String getBuildSystem(){
		return getStep();
	}
	
	public void setBuildSystem(@NotNull String s){
		setStep(s);
	}
	
	@NotNull
	public GraphProperty<String> getBuildSystemProperty(){
		return getStepProperty();
	}
}