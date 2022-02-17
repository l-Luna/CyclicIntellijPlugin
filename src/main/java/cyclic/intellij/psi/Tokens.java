package cyclic.intellij.psi;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.jetbrains.annotations.Contract;

import java.util.List;

import static org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory.createTokenSet;

public class Tokens{
	
	public static IElementType BAD_TOKEN_TYPE = TokenType.BAD_CHARACTER;
	
	public static final List<TokenIElementType> TOKEN_ELEMENT_TYPES = PSIElementTypeFactory.getTokenIElementTypes(CyclicLanguage.LANGUAGE);
	public static final List<RuleIElementType> RULE_ELEMENT_TYPES = PSIElementTypeFactory.getRuleIElementTypes(CyclicLanguage.LANGUAGE);
	
	public static final TokenSet KEYWORDS = createTokenSet(
			CyclicLanguage.LANGUAGE,
			
			CyclicLangLexer.BOOL,
			CyclicLangLexer.BYTE,
			CyclicLangLexer.SHORT,
			CyclicLangLexer.INT,
			CyclicLangLexer.LONG,
			CyclicLangLexer.FLOAT,
			CyclicLangLexer.DOUBLE,
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
			CyclicLangLexer.PERMITS,
			
			CyclicLangLexer.RETURN,
			CyclicLangLexer.THROW,
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
	
	public static final TokenSet OPERATORS = createTokenSet(
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
			
			CyclicLangLexer.PLUSPLUS,
			CyclicLangLexer.MINUSMINUS,
			
			CyclicLangLexer.STAR,
			CyclicLangLexer.SLASH,
			CyclicLangLexer.PLUS,
			CyclicLangLexer.MINUS,
			CyclicLangLexer.PERCENT
	);
	
	// used for syntax highlighting
	public static final TokenSet LITERALS = createTokenSet(
			CyclicLanguage.LANGUAGE,
			
			CyclicLangLexer.INTLIT,
			CyclicLangLexer.BOOLLIT,
			CyclicLangLexer.STRLIT,
			CyclicLangLexer.DECLIT
	);
	
	public static final TokenSet SYMBOLS = createTokenSet(
			CyclicLanguage.LANGUAGE,
			
			CyclicLangLexer.QUOTE,
			CyclicLangLexer.SEMICOLON,
			
			CyclicLangLexer.DASHARROW,
			CyclicLangLexer.EQARROW
	);
	
	public static final TokenSet PUNCTUATION = createTokenSet(
			CyclicLanguage.LANGUAGE,
			
			CyclicLangLexer.DOT,
			CyclicLangLexer.COMMA,
			CyclicLangLexer.COLON,
			CyclicLangLexer.ASSIGN,
			
			CyclicLangLexer.EXCLAMATION,
			CyclicLangLexer.QUESTION
	);
	
	public static final TokenSet WHITESPACES = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.WS);
	public static final TokenSet COMMENTS = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.SING_COMMENT);
	public static final TokenSet STRING_LITERALS = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.STRLIT);
	public static final TokenSet IDENTIFIERS = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.ID);
	
	// single-element sets
	public static final TokenSet RULE_IMPORT = TokenSet.create(getRuleFor(CyclicLangParser.RULE_importDecl));
	public static final TokenSet RULE_BIN_OP = TokenSet.create(getRuleFor(CyclicLangParser.RULE_binaryop));
	public static final TokenSet RULE_CALL = TokenSet.create(getRuleFor(CyclicLangParser.RULE_call));
	public static final TokenSet RULE_ID_PART = TokenSet.create(getRuleFor(CyclicLangParser.RULE_idPart));
	public static final TokenSet RULE_INITIALISATION = TokenSet.create(getRuleFor(CyclicLangParser.RULE_initialisation));
	public static final TokenSet RULE_CAST = TokenSet.create(getRuleFor(CyclicLangParser.RULE_cast));
	public static final TokenSet RULE_NEW_ARRAY = TokenSet.create(getRuleFor(CyclicLangParser.RULE_newArray));
	public static final TokenSet RULE_NEW_LIST_ARRAY = TokenSet.create(getRuleFor(CyclicLangParser.RULE_newListedArray));
	
	public static final TokenSet TOK_ASSIGN = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.ASSIGN);
	public static final TokenSet TOK_INSTANCEOF = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.INSTANCEOF);
	public static final TokenSet TOK_CLASS = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.CLASS);
	public static final TokenSet TOK_THIS = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.THIS);
	public static final TokenSet TOK_STRING = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.STRLIT);
	
	public static final TokenSet PARENTHESIS = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.LPAREN, CyclicLangLexer.RPAREN);
	public static final TokenSet SQ_BRACES = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.LSQUAR, CyclicLangLexer.RSQUAR);
	public static final TokenSet PRE_POST_OPS = TokenSet.create(
			getRuleFor(CyclicLangParser.RULE_prefixop), getRuleFor(CyclicLangParser.RULE_postfixop));
	
	// used for literal expression checking
	public static final TokenSet SEM_LITERALS = createTokenSet(CyclicLanguage.LANGUAGE,
			CyclicLangLexer.NULL,
			CyclicLangLexer.INTLIT,
			CyclicLangLexer.DECLIT,
			CyclicLangLexer.BOOLLIT
	);
	
	@Contract(pure = true) public static IElementType getFor(int type){
		return TOKEN_ELEMENT_TYPES.get(type);
	}
	
	@Contract(pure = true) public static IElementType getRuleFor(int type){
		return RULE_ELEMENT_TYPES.get(type);
	}
}
