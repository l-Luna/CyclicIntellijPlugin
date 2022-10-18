package cyclic.intellij.formatter.settings;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class CyclicCodeStyleSettings extends CustomCodeStyleSettings{
	
	protected CyclicCodeStyleSettings(CodeStyleSettings container){
		super("CyclicCodeStyleSettings", container);
	}
}