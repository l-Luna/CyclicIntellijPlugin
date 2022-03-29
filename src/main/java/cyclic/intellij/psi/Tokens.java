package cyclic.intellij.psi;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.stubs.StubTypes;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.jetbrains.annotations.Contract;

import java.util.List;

import static org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory.createTokenSet;

public class Tokens{
	
	public static IElementType BAD_TOKEN_TYPE = TokenType.BAD_CHARACTER;
	
	private static final List<TokenIElementType> TOKEN_ELEMENT_TYPES = PSIElementTypeFactory.getTokenIElementTypes(CyclicLanguage.LANGUAGE);
	private static final List<RuleIElementType> RULE_ELEMENT_TYPES = PSIElementTypeFactory.getRuleIElementTypes(CyclicLanguage.LANGUAGE);
	
	public static final TokenSet KEYWORDS = createTokenSet(
			CyclicLanguage.LANGUAGE,
			
			CyclicLangLexer.BOOL,
			CyclicLangLexer.BYTE,
			CyclicLangLexer.SHORT,
			CyclicLangLexer.CHAR,
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
			CyclicLangLexer.NATIVE,
			
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
			
			CyclicLangLexer.TRY,
			CyclicLangLexer.CATCH,
			CyclicLangLexer.FINALLY,
			
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
	
	public static final TokenSet CONTEXT_KEYWORDS = createTokenSet(
			CyclicLanguage.LANGUAGE,
			
			CyclicLangLexer.ANNOTATION,
			CyclicLangLexer.IN,
			CyclicLangLexer.OUT,
			CyclicLangLexer.SEALED,
			CyclicLangLexer.PERMITS
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
	public static final TokenSet TOK_THIS = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.THIS);
	public static final TokenSet TOK_STRING = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.STRLIT);
	
	public static final TokenSet TOK_CLASS = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.CLASS);
	public static final TokenSet TOK_INTERFACE = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.INTERFACE);
	public static final TokenSet TOK_ANNOTATION = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.ANNOTATION);
	public static final TokenSet TOK_AT = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.AT);
	public static final TokenSet TOK_ENUM = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.ENUM);
	public static final TokenSet TOK_RECORD = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.RECORD);
	public static final TokenSet TOK_SINGLE = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.SINGLE);
	
	public static final TokenSet TOK_NULL = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.NULL);
	public static final TokenSet TOK_INTLIT = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.INTLIT);
	public static final TokenSet TOK_DECLIT = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.DECLIT);
	public static final TokenSet TOK_BOOLLIT = createTokenSet(CyclicLanguage.LANGUAGE, CyclicLangLexer.BOOLLIT);
	
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
	
	@Contract(pure = true)
	public static IElementType getFor(int type){
		return TOKEN_ELEMENT_TYPES.get(type);
	}
	
	@Contract(pure = true)
	public static IElementType getRuleFor(int type){
		if(type == CyclicLangParser.RULE_classDecl)
			return StubTypes.CYC_TYPE;
		
		else if(type == CyclicLangParser.RULE_member)
			return StubTypes.CYC_MEMBER;
		else if(type == CyclicLangParser.RULE_recordComponents)
			return StubTypes.CYC_RECORD_COMPONENTS;
		else if(type == CyclicLangParser.RULE_parameter)
			return StubTypes.CYC_PARAMETER;
		else if(type == CyclicLangParser.RULE_modifiers)
			return StubTypes.CYC_MODIFIER_LIST;
		else if(type == CyclicLangParser.RULE_function)
			return StubTypes.CYC_METHOD;
		else if(type == CyclicLangParser.RULE_varDecl)
			return StubTypes.CYC_FIELD;
		else if(type == CyclicLangParser.RULE_parameters)
			return StubTypes.CYC_PARAMETERS_LIST;
		
		else if(type == CyclicLangParser.RULE_objectExtends)
			return StubTypes.CYC_EXTENDS_LIST;
		else if(type == CyclicLangParser.RULE_objectImplements)
			return StubTypes.CYC_IMPLEMENTS_LIST;
		else if(type == CyclicLangParser.RULE_objectPermits)
			return StubTypes.CYC_PERMITS_LIST;
		
		return RULE_ELEMENT_TYPES.get(type);
	}
}
