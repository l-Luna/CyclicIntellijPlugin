package cyclic.intellij.model.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.NlsContexts.ConfigurableName;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.model.CyclicLanguageLevel;
import cyclic.intellij.model.LangLevelConverter;
import cyclic.intellij.model.config.CompilerOptions;
import cyclic.intellij.model.sdks.CyclicSdk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class CyclicFacetConfiguration implements FacetConfiguration, PersistentStateComponent<CyclicFacetConfiguration>{
	
	@OptionTag(converter = LangLevelConverter.class)
	public CyclicLanguageLevel cyclicVersion = CyclicLanguageLevel.v0_1_0;
	
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
		private ComboBox<CyclicLanguageLevel> versions;
		private ComboBox<CyclicSdk> compilerPaths;
		
		CyclicFacetTab(CyclicFacet facet){
			this.facet = facet;
		}
		
		public @NotNull JComponent createComponent(){
			if(panel == null){
				versions = CompilerOptions.languageLevelChooser();
				var curSdk = WorkspaceSdk.getFor(facet.getModule().getProject()).currentOrDummy();
				compilerPaths = CompilerOptions.sdkChooser(curSdk);
				
				panel = new JPanel(new VerticalFlowLayout());
				panel.add(new JLabel(CyclicBundle.message("label.cyclic.version")));
				panel.add(this.versions);
				panel.add(new JLabel(CyclicBundle.message("label.workspace.compiler.path")));
				panel.add(this.compilerPaths);
			}
			
			return panel;
		}
		
		public boolean isModified(){
			if(facet == null)
				return true;
			var setVersion = facet.getConfiguration().cyclicVersion;
			if(!Objects.equals(setVersion, versions.getItem()))
				return true;
			
			var setPath = WorkspaceSdk.getFor(facet.getModule().getProject()).compilerPath;
			return !Objects.equals(setPath, compilerPaths.getItem().path);
		}
		
		public void apply(){
			var ver = versions.getItem();
			facet.getConfiguration().cyclicVersion = ver != null ? ver : CyclicLanguageLevel.v0_1_0;
			WorkspaceSdk.getFor(facet.getModule().getProject()).compilerPath = compilerPaths.getItem().path;
		}
		
		public @ConfigurableName String getDisplayName(){
			return CyclicBundle.message("configurable.name.cyclic.facet");
		}
	}
}