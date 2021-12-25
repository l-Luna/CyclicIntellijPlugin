package cyclic.intellij.parser;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.tree.IElementType;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.psi.Tokens;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicSyntaxHighlighter extends SyntaxHighlighterFactory implements SyntaxHighlighter{
	
	private static final TextAttributesKey ID = TextAttributesKey.createTextAttributesKey("Cyclic-ID", DefaultLanguageHighlighterColors.IDENTIFIER);
	private static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("Cyclic-Keyword", DefaultLanguageHighlighterColors.KEYWORD);
	private static final TextAttributesKey NUMLIT = TextAttributesKey.createTextAttributesKey("Cyclic-Number-Literal", DefaultLanguageHighlighterColors.NUMBER);
	private static final TextAttributesKey STRLIT = TextAttributesKey.createTextAttributesKey("Cyclic-String-Literal", DefaultLanguageHighlighterColors.STRING);
	private static final TextAttributesKey SYMBOLS = TextAttributesKey.createTextAttributesKey("Cyclic-Symbols", DefaultLanguageHighlighterColors.SEMICOLON);
	private static final TextAttributesKey DOTCOMMA = TextAttributesKey.createTextAttributesKey("Cyclic-Dot-Comma", DefaultLanguageHighlighterColors.DOT);
	private static final TextAttributesKey OPERATORS = TextAttributesKey.createTextAttributesKey("Cyclic-Operators", DefaultLanguageHighlighterColors.OPERATION_SIGN);
	private static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("Cyclic-Comment", DefaultLanguageHighlighterColors.LINE_COMMENT);
	
	@NotNull
	@Override public Lexer getHighlightingLexer(){
		CyclicLangLexer lexer = new CyclicLangLexer(null);
		return new LexerAdapter(lexer);
	}
	
	public TextAttributesKey @NotNull [] getTokenHighlights(IElementType element){
		if(Tokens.IDENTIFIERS.contains(element))
			return array(ID);
		else if(Tokens.KEYWORDS.contains(element))
			return array(KEYWORD);
		else if(Tokens.getFor(CyclicLangLexer.STRLIT) == element)
			return array(STRLIT);
		else if(Tokens.LITERALS.contains(element))
			return array(NUMLIT); // Add boolean literal formatting
		else if(Tokens.PUNCTUATION.contains(element))
			return array(DOTCOMMA);
		else if(Tokens.SYMBOLS.contains(element))
			return array(SYMBOLS);
		else if(Tokens.OPERATORS.contains(element))
			return array(OPERATORS);
		else if(Tokens.COMMENTS.contains(element))
			return array(COMMENT);
		
		return new TextAttributesKey[0];
	}
	
	public @NotNull com.intellij.openapi.fileTypes.SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile){
		return new CyclicSyntaxHighlighter();
	}
	
	public TextAttributesKey[] array(TextAttributesKey key){
		return new TextAttributesKey[]{key};
	}
}