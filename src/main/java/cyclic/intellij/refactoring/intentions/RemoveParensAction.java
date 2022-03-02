package cyclic.intellij.refactoring.intentions;

import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.psi.elements.CycExpression;
import cyclic.intellij.psi.expressions.CycParenthesisedExpr;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class RemoveParensAction extends AbstractExprAction{
	
	public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file, CycExpression expression){
		return expression instanceof CycParenthesisedExpr && PsiUtils.childOfType(expression, CycExpression.class).isPresent();
	}
	
	public void invoke(@NotNull Project project, Editor editor, PsiFile file, CycExpression expression) throws IncorrectOperationException{
		expression.replace(PsiUtils.childOfType(expression, CycExpression.class).orElseThrow());
	}
	
	public @IntentionName @NotNull String getText(){
		return "Remove parentheses";
	}
	
	public @NotNull @IntentionFamilyName String getFamilyName(){
		return "Refactor";
	}
}
