package cyclic.intellij.sdks.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsContexts.ConfigurableName;
import com.intellij.openapi.util.io.FileUtil;
import cyclic.intellij.sdks.CyclicSdk;
import cyclic.intellij.sdks.SdkUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor;

public class CyclicSdkConfigurable implements Configurable{
	
	private final JLabel compilerJarLabel;
	private final TextFieldWithBrowseButton compilerJarPath;
	
	private final JPanel panel;
	
	private final CyclicSdk sdk;
	
	public CyclicSdkConfigurable(CyclicSdk sdk){
		this.sdk = sdk;
		compilerJarLabel = new JLabel("Compiler JAR path:");
		compilerJarPath = new TextFieldWithBrowseButton();
		compilerJarPath.addBrowseFolderListener(
				"Select Cyclic Compiler",
				"Specifies the JAR file used to compile Cyclic files",
				null,
				createSingleFileDescriptor().withFileFilter(x -> x.getName().endsWith(".jar")));
		
		compilerJarLabel.setLabelFor(compilerJarPath);
		
		panel = new JPanel(new VerticalFlowLayout());
		panel.add(compilerJarLabel);
		panel.add(compilerJarPath);
	}
	
	public @ConfigurableName String getDisplayName(){
		return "Configure Cyclic Compiler";
	}
	
	public @Nullable JComponent createComponent(){
		return panel;
	}
	
	public boolean isModified(){
		return !sdk.path.equals(FileUtil.toSystemIndependentName(compilerJarPath.getText()));
	}
	
	public void apply() throws ConfigurationException{
		sdk.copySettings(SdkUtils.readJarInfo(compilerJarPath.getText()));
	}
}