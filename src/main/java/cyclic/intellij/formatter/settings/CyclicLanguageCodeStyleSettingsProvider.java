package cyclic.intellij.formatter.settings;

import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.*;
import cyclic.intellij.CyclicLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class CyclicLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider{
	
	public @NotNull Language getLanguage(){
		return CyclicLanguage.LANGUAGE;
	}
	
	public @Nullable String getCodeSample(@NotNull SettingsType settingsType){
		return
            """
			class C{
				int value = 3+3;
			}
			""";
	}
	
	public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType){
		if(settingsType == SettingsType.SPACING_SETTINGS){
			consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS");
		}
	}
}