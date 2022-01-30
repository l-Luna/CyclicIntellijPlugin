package cyclic.intellij.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.*;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.jetbrains.annotations.NotNull;

public class CyclicParserDefinition implements ParserDefinition{
	
	public static final IFileElementType FILE = new IFileElementType(CyclicLanguage.LANGUAGE);
	
	public CyclicParserDefinition(){
		PSIElementTypeFactory.defineLanguageIElementTypes(CyclicLanguage.LANGUAGE, CyclicLangLexer.tokenNames, CyclicLangLexer.ruleNames);
	}
	
	public @NotNull Lexer createLexer(Project project){
		return new LexerAdapter(new CyclicLangLexer(null));
	}
	
	public @NotNull PsiParser createParser(Project project){
		return new ParserAdapter();
	}
	
	public @NotNull IFileElementType getFileNodeType(){
		return FILE;
	}
	
	public @NotNull TokenSet getWhitespaceTokens(){
		return Tokens.WHITESPACES;
	}
	
	public @NotNull TokenSet getCommentTokens(){
		return Tokens.COMMENTS;
	}
	
	public @NotNull TokenSet getStringLiteralElements(){
		return Tokens.STRING_LITERALS;
	}
	
	public @NotNull PsiElement createElement(ASTNode node){
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_file))
			return new CycFileWrapper(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_classDecl))
			return new CycType(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_id))
			return new CycId(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_idPart))
			return new CycIdPart(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_packageDecl))
			return new CycPackageStatement(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_importDecl))
			return new CycImportStatement(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_rawType))
			return new CycRawTypeRef(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_member))
			return new CycMember(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_function))
			return new CycMethod(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_statement))
			return new CycStatement(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_value))
			return new CycExpression(node);
		return new CycElement(node);
	}
	
	public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider){
		return new CycFile(viewProvider);
	}
}
