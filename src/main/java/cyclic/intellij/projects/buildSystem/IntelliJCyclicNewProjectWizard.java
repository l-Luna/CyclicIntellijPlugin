package cyclic.intellij.projects.buildSystem;

import com.intellij.ide.projectWizard.NewProjectWizardCollector;
import com.intellij.ide.projectWizard.generators.IntelliJNewProjectWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.NewProjectWizardStep;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.dsl.builder.ComboBoxKt;
import com.intellij.ui.dsl.builder.Panel;
import com.intellij.ui.dsl.builder.TextFieldKt;
import cyclic.intellij.model.config.CompilerOptions;
import cyclic.intellij.model.facet.WorkspaceSdk;
import cyclic.intellij.model.sdks.CyclicSdk;
import cyclic.intellij.model.sdks.CyclicSdks;
import cyclic.intellij.projects.BuildSystemCyclicNewProjectWizard;
import cyclic.intellij.projects.CyclicNewProjectStep;
import kotlin.Unit;
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
		
		private ComboBox<CyclicSdk> sdkChooser;
		
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
			
			// cyclic SDKs are project-wide // we check in customOptions() for null
			//noinspection ConstantConditions
			WorkspaceSdk.getFor(project).compilerPath = ((CyclicSdk)sdkChooser.getSelectedItem()).path;
			
			addDefaultCode(builder, project);
			
			builder.commit(project);
			NewProjectWizardCollector.BuildSystem.INSTANCE.logSdkFinished(getParent(), getSdk());
		}
		
		public void addDefaultCode(CyclicModuleBuilder builder, Project project){
			if(getAddSampleCode())
				builder.openWhenProjectCreated = Utils.tryAddSampleCode(project, getContentRoot() + "/src");
		}
		
		public void customOptions(@NotNull Panel panel){
			panel.row("Cyclic SDK:", row -> {
				sdkChooser = CompilerOptions.sdkChooser(null);
				row.cell(sdkChooser).validation(() -> {
					if(sdkChooser.getSelectedItem() == null
							|| sdkChooser.getSelectedIndex() == -1
							|| sdkChooser.getSelectedItem() == CyclicSdks.DUMMY_SDK)
						return new ValidationInfo("Please select a Cyclic SDK", sdkChooser);
					return null;
				});
				ComboBoxKt.columns(sdkChooser, TextFieldKt.COLUMNS_MEDIUM);
				return Unit.INSTANCE;
			});
		}
	}
}