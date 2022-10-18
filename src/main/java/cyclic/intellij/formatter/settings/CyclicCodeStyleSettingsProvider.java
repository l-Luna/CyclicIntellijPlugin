package cyclic.intellij.formatter.settings;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.lang.Language;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import cyclic.intellij.CyclicLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicCodeStyleSettingsProvider extends CodeStyleSettingsProvider{
	
	public @Nullable CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings){
		return new CyclicCodeStyleSettings(settings);
	}
	
	public @Nullable @NlsContexts.ConfigurableName String getConfigurableDisplayName(){
		return "Cyclic";
	}
	
	public @NotNull CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings, @NotNull CodeStyleSettings modelSettings){
		return new CodeStyleAbstractConfigurable(settings, modelSettings, getConfigurableDisplayName()){
			protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings){
				return new CyclicCodeStylePanel(getCurrentSettings(), settings);
			}
		};
	}
	
	private static class CyclicCodeStylePanel extends TabbedLanguageCodeStylePanel{
		
		protected CyclicCodeStylePanel(CodeStyleSettings currentSettings, @NotNull CodeStyleSettings settings){
			super(CyclicLanguage.LANGUAGE, currentSettings, settings);
		}
	}
}