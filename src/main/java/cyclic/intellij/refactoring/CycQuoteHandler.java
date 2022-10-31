package cyclic.intellij.refactoring;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangLexer;

import static org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory.createTokenSet;

public class CycQuoteHandler extends SimpleTokenSetQuoteHandler{
	
	public CycQuoteHandler(){
		super(createTokenSet(
				CyclicLanguage.LANGUAGE,
				
				CyclicLangLexer.STRLIT,
				CyclicLangLexer.CHARLIT,
				CyclicLangLexer.QUOTE,
				CyclicLangLexer.APOSTRAPHE
		));
	}
}