package cyclic.intellij.templates.live;

import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.parser.CyclicSyntaxHighlighter;
import cyclic.intellij.psi.CycCodeHolder;
import cyclic.intellij.psi.ast.CycImportStatement;
import cyclic.intellij.psi.ast.CycPackageStatement;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.ast.expressions.CycLiteralExpr;
import cyclic.intellij.psi.ast.statements.CycStatementWrapper;
import cyclic.intellij.psi.ast.types.CycMemberWrapper;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CyclicTemplateContextType extends TemplateContextType{
	
	public CyclicTemplateContextType(@NotNull @NonNls String id,
	                                 @NlsContexts.Label @NotNull String presentableName,
	                                 @Nullable Class<? extends TemplateContextType> baseContextType){
		super(id, presentableName, baseContextType);
	}
	
	public boolean isInContext(@NotNull TemplateActionContext ctx){
		var file = ctx.getFile();
		var offset = ctx.getStartOffset();
		
		if(!PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(CyclicLanguage.LANGUAGE))
			return false;
		
		PsiElement element = file.findElementAt(offset);
		if(element == null)
			element = file.findElementAt(offset - 1);
		if(element == null)
			return false;
		
		if(element instanceof PsiWhiteSpace)
			return false;
		if(PsiTreeUtil.getParentOfType(element, CycPackageStatement.class, CycImportStatement.class) != null)
			return false;
		
		if(PsiTreeUtil.getParentOfType(element, PsiComment.class, false) != null)
			return isCommentInContext();
		
		return isInContext(element);
	}
	
	protected abstract boolean isInContext(@NotNull PsiElement e);
	
	protected boolean isCommentInContext(){
		return false;
	}
	
	public @Nullable SyntaxHighlighter createHighlighter(){
		return new CyclicSyntaxHighlighter();
	}
	
	public static class Generic extends CyclicTemplateContextType{
		
		public Generic(){
			super("CYCLIC", CyclicBundle.message("liveTemplate.type.generic"), EverywhereContextType.class);
		}
		
		protected boolean isInContext(@NotNull PsiElement e){
			return true;
		}
		
		protected boolean isCommentInContext(){
			return true;
		}
	}
	
	public static class Declaration extends CyclicTemplateContextType{
		
		public Declaration(){
			super("CYCLIC_DECLARATION", CyclicBundle.message("liveTemplate.type.declaration"), Generic.class);
		}
		
		protected boolean isInContext(@NotNull PsiElement e){
			return PsiTreeUtil.getParentOfType(e, CycMemberWrapper.class, false, CycCodeHolder.class) != null;
		}
	}
	
	public static class Statement extends CyclicTemplateContextType{
		
		public Statement(){
			super("CYCLIC_STATEMENT", CyclicBundle.message("liveTemplate.type.statement"), Generic.class);
		}
		
		protected boolean isInContext(@NotNull PsiElement e){
			CycStatementWrapper stat = PsiTreeUtil.getParentOfType(e, CycStatementWrapper.class, false);
			return stat != null && stat.getTextOffset() == e.getTextOffset();
		}
	}
	
	public static class Expression extends CyclicTemplateContextType{
		
		public Expression(){
			super("CYCLIC_EXPRESSION", CyclicBundle.message("liveTemplate.type.expression"), Generic.class);
		}
		
		protected boolean isInContext(@NotNull PsiElement e){
			CycExpression parent = PsiTreeUtil.getParentOfType(e, CycExpression.class);
			return parent != null && !(parent instanceof CycLiteralExpr);
		}
	}
	
	public static class Comment extends CyclicTemplateContextType{
		
		public Comment(){
			super("CYCLIC_COMMENT", CyclicBundle.message("liveTemplate.type.comment"), Generic.class);
		}
		
		protected boolean isInContext(@NotNull PsiElement e){
			return false;
		}
		
		protected boolean isCommentInContext(){
			return true;
		}
	}
}