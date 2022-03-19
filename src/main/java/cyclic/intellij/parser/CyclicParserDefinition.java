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
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.ast.*;
import cyclic.intellij.psi.ast.common.*;
import cyclic.intellij.psi.ast.expressions.*;
import cyclic.intellij.psi.ast.statements.*;
import cyclic.intellij.psi.ast.types.*;
import cyclic.intellij.psi.stubs.CycFileStub;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class CyclicParserDefinition implements ParserDefinition{
	
	public static final IStubFileElementType<CycFileStub> FILE =
			new IStubFileElementType<>("cyclic.FILE", CyclicLanguage.LANGUAGE){
				public @NonNls @NotNull String getExternalId(){
					return "cyclic.FILE";
				}
			};
	
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
		var type = node.getElementType();
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_file))
			return new CycFileWrapper(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_classDecl))
			return new CycType(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_id))
			return new CycId(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_idPart))
			return new CycIdPart(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_packageDecl))
			return new CycPackageStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_importDecl))
			return new CycImportStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_imports))
			return new CycImportList(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_annotation))
			return new CycAnnotation(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_rawType))
			return new CycRawTypeRef(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_type))
			return new CycTypeRef(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_typeOrInferred))
			return new CycTypeRefOrInferred(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_member))
			return new CycMember(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_modifiers))
			return new CycModifierList(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_modifier))
			return new CycModifier(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_function))
			return new CycMethod(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_statement))
			return new CycStatementWrapper(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_value))
			return createExpr(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_call))
			return new CycCall(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_initialisation))
			return new CycInitialisation(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_varAssignment))
			return new CycVariableAssignment(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_objectExtends))
			return new CycExtendsClause(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_objectImplements))
			return new CycImplementsClause(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_objectPermits))
			return new CycPermitsClause(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_block))
			return new CycBlock(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_varDecl))
			return new CycVariableDef(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_parameter))
			return new CycParameter(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_recordComponents))
			return new CycRecordComponents(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_arguments))
			return new CycArgumentsList(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_parameters))
			return new CycParametersList(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_binaryop))
			return new CycBinaryOp(node);
		
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_assertStatement))
			return new CycAssertStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_ctorCall))
			return new CycConstructorCallStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_doWhile))
			return new CycDoWhileStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_foreachStatement))
			return new CycForeachStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_forStatement))
			return new CycForStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_ifStatement))
			return new CycIfStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_elseStatement))
			return new CycElseElement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_returnStatement))
			return new CycReturnStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_switchStatement))
			return new CycSwitchStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_throwStatement))
			return new CycThrowStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_varAssignment))
			return new CycVarAssignStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_varIncrement))
			return new CycVarIncrementStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_whileStatement))
			return new CycWhileStatement(node);
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_yieldStatement))
			return new CycYieldStatement(node);
		return new CycAstElement(node);
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