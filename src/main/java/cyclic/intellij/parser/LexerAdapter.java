package cyclic.intellij.parser;

import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.v4.runtime.Lexer;

public class LexerAdapter extends ANTLRLexerAdaptor{
	
	public LexerAdapter(){
		this(new CyclicLangLexer(null));
	}
	
	public LexerAdapter(Lexer lexer){
		super(CyclicLanguage.LANGUAGE, lexer);
	}
}