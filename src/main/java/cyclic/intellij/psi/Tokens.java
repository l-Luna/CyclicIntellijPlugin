package cyclic.intellij.psi;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.jetbrains.annotations.Contract;

import java.util.List;

public class Tokens{
	
	public static IElementType BAD_TOKEN_TYPE = TokenType.BAD_CHARACTER;
	
	public static final List<TokenIElementType> TOKEN_ELEMENT_TYPES = PSIElementTypeFactory.getTokenIElementTypes(CyclicLanguage.LANGUAGE);
	public static final List<RuleIElementType> RULE_ELEMENT_TYPES = PSIElementTypeFactory.getRuleIElementTypes(CyclicLanguage.LANGUAGE);
	
	public static final TokenSet KEYWORDS = PSIElementTypeFactory.createTokenSet(
			CyclicLanguage.LANGUAGE,
			
			CyclicLangLexer.BOOL,
			CyclicLangLexer.BYTE,
			CyclicLangLexer.DEC,
			CyclicLangLexer.INT,
			CyclicLangLexer.VOID,
			
			CyclicLangLexer.VAR,
			CyclicLangLexer.VAL,
			
			CyclicLangLexer.PRIVATE,
			CyclicLangLexer.PROTECTED,
			CyclicLangLexer.PUBLIC,
			
			CyclicLangLexer.FINAL,
			CyclicLangLexer.STATIC,
			CyclicLangLexer.ABSTRACT,
			CyclicLangLexer.SYNCHRONISED,
			
			CyclicLangLexer.CLASS,
			CyclicLangLexer.INTERFACE,
			CyclicLangLexer.ENUM,
			CyclicLangLexer.AT,
			CyclicLangLexer.RECORD,
			CyclicLangLexer.SINGLE,
			
			CyclicLangLexer.IMPORT,
			CyclicLangLexer.PACKAGE,
			CyclicLangLexer.EXTENDS,
			CyclicLangLexer.IMPLEMENTS,
			
			CyclicLangLexer.RETURN,
			CyclicLangLexer.NEW,
			CyclicLangLexer.ASSERT,
			
			CyclicLangLexer.THIS,
			
			CyclicLangLexer.SWITCH,
			CyclicLangLexer.DEFAULT,
			CyclicLangLexer.CASE,
			CyclicLangLexer.WHILE,
			CyclicLangLexer.DO,
			CyclicLangLexer.IF,
			CyclicLangLexer.FOR,
			CyclicLangLexer.ELSE,
			
			CyclicLangLexer.NULL,
			CyclicLangLexer.TRUE,
			CyclicLangLexer.FALSE
	);
	
	public static final TokenSet OPERATORS = PSIElementTypeFactory.createTokenSet(
			CyclicLanguage.LANGUAGE,
			
			CyclicLangLexer.EQUAL,
			CyclicLangLexer.INEQUAL,
			CyclicLangLexer.GREATEREQ,
			CyclicLangLexer.LESSEREQ,
			CyclicLangLexer.GREATER,
			CyclicLangLexer.LESSER,
			
			CyclicLangLexer.AND,
			CyclicLangLexer.OR,
			CyclicLangLexer.BITAND,
			CyclicLangLexer.BITOR,
			
			CyclicLangLexer.UP,
			CyclicLangLexer.PASS,
			
			CyclicLangLexer.STAR,
			CyclicLangLexer.SLASH,
			CyclicLangLexer.PLUS,
			CyclicLangLexer.MINUS,
			CyclicLangLexer.PERCENT
	);
	
	public static final TokenSet LITERALS = PSIElementTypeFactory.createTokenSet(
			CyclicLanguage.LANGUAGE,
			
			CyclicLangLexer.INTLIT,
			CyclicLangLexer.BOOLLIT,
			CyclicLangLexer.STRLIT,
			CyclicLangLexer.DECLIT
	);
	
	public static final TokenSet SYMBOLS = PSIElementTypeFactory.createTokenSet(
			CyclicLanguage.LANGUAGE,
			
			CyclicLangLexer.QUOTE,
			CyclicLangLexer.SEMICOLON,
			CyclicLangLexer.ASSIGN,
			
			CyclicLangLexer.DASHARROW,
			CyclicLangLexer.EQARROW
	);
	
	public static final TokenSet PUNCTUATION = PSIElementTypeFactory.createTokenSet(
			CyclicLanguage.LANGUAGE,
			
			CyclicLangLexer.DOT,
			CyclicLangLexer.COMMA,
			
			CyclicLangLexer.EXCLAMATION,
			CyclicLangLexer.QUESTION
	);
	
	public static final TokenSet BRACES = PSIElementTypeFactory.createTokenSet(
			CyclicLanguage.LANGUAGE,
			
			CyclicLangLexer.LPAREN,
			CyclicLangLexer.RPAREN,
			CyclicLangLexer.LBRACE,
			CyclicLangLexer.RBRACE,
			CyclicLangLexer.LSQUAR,
			CyclicLangLexer.RSQUAR
	);
	
	public static final TokenSet WHITESPACES = PSIElementTypeFactory.createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.WS);
	public static final TokenSet COMMENTS = PSIElementTypeFactory.createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.SING_COMMENT);
	public static final TokenSet STRING_LITERALS = PSIElementTypeFactory.createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.STRLIT);
	public static final TokenSet IDENTIFIERS = PSIElementTypeFactory.createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.ID);
	
	@Contract(pure = true) public static IElementType getFor(int type){
		return TOKEN_ELEMENT_TYPES.get(type);
	}
	
	@Contract(pure = true) public static IElementType getRuleFor(int type){
		return RULE_ELEMENT_TYPES.get(type);
	}
}
