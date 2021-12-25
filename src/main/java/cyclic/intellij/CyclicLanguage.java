package cyclic.intellij;

import com.intellij.lang.Language;

public class CyclicLanguage extends Language{
	
	public static final CyclicLanguage LANGUAGE = new CyclicLanguage();
	
	protected CyclicLanguage(){
		super("Cyclic");
	}
}