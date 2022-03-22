package cyclic.intellij.refactoring.intentions;

import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.ast.expressions.CycParenthesisedExpr;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class AddParensAction extends AbstractExprAction{
	
	public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file, CycExpression expression){
		return !(expression instanceof CycParenthesisedExpr)
				&& expression.getParent() instanceof CycExpression
				&& !(expression.getParent() instanceof CycParenthesisedExpr);
	}
	
	public void invoke(@NotNull Project project, Editor editor, PsiFile file, CycExpression expression) throws IncorrectOperationException{
		expression.replace(PsiUtils.createExpressionFromText(expression.getParent(), "(" + expression.getText() + ")"));
	}
	
	public @IntentionName @NotNull String getText(){
		return CyclicBundle.message("intention.text.parens.add");
	}
	
	public @NotNull @IntentionFamilyName String getFamilyName(){
		return CyclicBundle.message("intention.familyName.refactor");
	}
}
