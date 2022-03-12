package cyclic.intellij.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.Tokens;
import org.antlr.intellij.adaptor.parser.ANTLRParseTreeToPSIConverter;
import org.antlr.intellij.adaptor.parser.ANTLRParserAdaptor;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParserAdapter extends ANTLRParserAdaptor{
	
	public ParserAdapter(){
		super(CyclicLanguage.LANGUAGE, new CyclicLangParser(null));
	}
	
	protected ParseTree parse(Parser parser, IElementType root){
		CyclicLangParser cyclicParser = (CyclicLangParser)parser;
		
		if(root == Tokens.getRuleFor(CyclicLangParser.RULE_type))
			return cyclicParser.type();
		if(root == Tokens.getRuleFor(CyclicLangParser.RULE_value))
			return cyclicParser.value();
		if(root == Tokens.getRuleFor(CyclicLangParser.RULE_importDecl))
			return cyclicParser.importDecl();
		if(root == Tokens.getRuleFor(CyclicLangParser.RULE_objectExtends))
			return cyclicParser.objectExtends();
		if(root == Tokens.getRuleFor(CyclicLangParser.RULE_objectImplements))
			return cyclicParser.objectImplements();
		if(root == Tokens.getFor(CyclicLangLexer.ID))
			return cyclicParser.id();
		return cyclicParser.file();
	}
	
	protected ANTLRParseTreeToPSIConverter createListener(Parser parser, IElementType root, PsiBuilder builder){
		return new TypeSubstitutingAntlrParseTreeToPsiTreeConverter(language, parser, builder);
	}
}