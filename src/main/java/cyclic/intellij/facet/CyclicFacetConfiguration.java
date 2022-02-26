package cyclic.intellij.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.NlsContexts.ConfigurableName;
import com.intellij.openapi.util.Version;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import cyclic.intellij.sdks.CyclicSdks;
import cyclic.intellij.sdks.VersionConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CyclicFacetConfiguration implements FacetConfiguration, PersistentStateComponent<CyclicFacetConfiguration>{
	
	@OptionTag(converter = VersionConverter.class)
	public Version cyclicVersion = new Version(0, 0, 0);
	
	public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager){
		return new FacetEditorTab[]{new CyclicFacetTab((CyclicFacet)editorContext.getFacet())};
	}
	
	public @Nullable CyclicFacetConfiguration getState(){
		return this;
	}
	
	public void loadState(@NotNull CyclicFacetConfiguration state){
		XmlSerializerUtil.copyBean(state, this);
	}
	
	static class CyclicFacetTab extends FacetEditorTab{
		
		private final CyclicFacet facet;
		private JPanel panel;
		private ComboBox<String> versions;
		private ComboBox<String> compilerPaths;
		
		CyclicFacetTab(CyclicFacet facet){
			this.facet = facet;
		}
		
		public @NotNull JComponent createComponent(){
			if(panel == null){
				// TODO: grab versions from elsewhere
				List<String> shownVersions = CyclicSdks.getInstance().compilers
						.stream()
						.map(x -> x.version.toString())
						.collect(Collectors.toCollection(ArrayList::new));
				var setVersion = facet.getConfiguration().cyclicVersion.toString();
				if(!shownVersions.contains(setVersion))
					shownVersions.add(0, setVersion);
				versions = new ComboBox<>(shownVersions.toArray(String[]::new));
				
				List<String> shownPaths = CyclicSdks.getInstance().compilers
						.stream()
						.map(x -> x.path)
						.collect(Collectors.toCollection(ArrayList::new));
				var setPath = WorkspaceSdk.getFor(facet.getModule().getProject()).compilerPath;
				if(!shownPaths.contains(setPath))
					shownPaths.add(0, setPath);
				compilerPaths = new ComboBox<>(shownPaths.toArray(String[]::new));
				
				panel = new JPanel(new VerticalFlowLayout());
				panel.add(new JLabel("Cyclic Version:"));
				panel.add(this.versions);
				panel.add(new JLabel("Cyclic Compiler path (workspace-specific):"));
				panel.add(this.compilerPaths);
			}
			
			return panel;
		}
		
		public boolean isModified(){
			var setVersion = facet.getConfiguration().cyclicVersion.toString();
			if(!setVersion.equals(versions.getItem()))
				return true;
			
			var setPath = WorkspaceSdk.getFor(facet.getModule().getProject()).compilerPath;
			return !setPath.equals(compilerPaths.getItem());
		}
		
		public void apply(){
			var ver = Version.parseVersion(versions.getItem());
			facet.getConfiguration().cyclicVersion = ver != null ? ver : new Version(0, 0, 0);
			WorkspaceSdk.getFor(facet.getModule().getProject()).compilerPath = compilerPaths.getItem();
		}
		
		public @ConfigurableName String getDisplayName(){
			return "Configure Cyclic Facet";
		}
	}
}