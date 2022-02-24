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
import cyclic.intellij.psi.expressions.*;
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
		// TODO: replace with switch (by rule index)
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
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_imports))
			return new CycImportList(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_rawType))
			return new CycRawTypeRef(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_type))
			return new CycTypeRef(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_member))
			return new CycMember(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_modifiers))
			return new CycModifierList(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_modifier))
			return new CycModifier(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_function))
			return new CycMethod(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_statement))
			return new CycStatement(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_value))
			return createExpr(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_call))
			return new CycCall(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_varAssignment))
			return new CycVariableAssignment(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_objectExtends))
			return new CycExtendsClause(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_objectImplements))
			return new CycImplementsClause(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_objectPermits))
			return new CycPermitsClause(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_block))
			return new CycBlock(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_varDecl))
			return new CycVariableDef(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_parameter))
			return new CycParameter(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_recordComponents))
			return new CycRecordComponents(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_arguments))
			return new CycArgumentsList(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_parameters))
			return new CycParametersList(node);
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_binaryop))
			return new CycBinaryOp(node);
		
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_foreachStatement))
			return new CycForeachLoop(node);
		return new CycElement(node);
	}
	
	public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider){
		return new CycFile(viewProvider);
	}
	
	private PsiElement createExpr(ASTNode node){
		// expression tag info is lost during conversion to a PSI AST rn
		// so, we "guess" what it is
		// TODO: rewrite to not be a big if/else chain
		if(node.findChildByType(Tokens.TOK_ASSIGN) != null)
			return new CycAssignExpr(node);
		if(node.findChildByType(Tokens.RULE_BIN_OP) != null)
			return new CycBinaryExpr(node);
		if(node.findChildByType(Tokens.RULE_CALL) != null)
			return new CycCallExpr(node);
		if(node.findChildByType(Tokens.TOK_INSTANCEOF) != null)
			return new CycInstanceOfExpr(node);
		if(node.findChildByType(Tokens.SQ_BRACES) != null)
			return new CycArrayIndexExpr(node);
		if(node.findChildByType(Tokens.RULE_ID_PART) != null)
			return new CycIdExpr(node);
		if(node.findChildByType(Tokens.RULE_INITIALISATION) != null)
			return new CycInitialisationExpr(node);
		if(node.findChildByType(Tokens.PARENTHESIS) != null)
			return new CycParenthesisedExpr(node);
		if(node.findChildByType(Tokens.TOK_CLASS) != null)
			return new CycClassLiteralExpr(node);
		if(node.findChildByType(Tokens.RULE_CAST) != null)
			return new CycCastExpr(node);
		if(node.findChildByType(Tokens.PRE_POST_OPS) != null)
			return new CycAffixOpExpr(node);
		if(node.findChildByType(Tokens.RULE_NEW_ARRAY) != null)
			return new CycNewArrayExpr(node);
		if(node.findChildByType(Tokens.RULE_NEW_LIST_ARRAY) != null)
			return new CycNewListArrayExpr(node);
		if(node.findChildByType(Tokens.TOK_THIS) != null)
			return new CycThisExpr(node);
		if(node.findChildByType(Tokens.SEM_LITERALS) != null)
			return new CycLiteralExpr(node);
		if(node.findChildByType(Tokens.TOK_STRING) != null)
			return new CycStringLiteralExpr(node);
		return new CycExpression(node);
	}
}