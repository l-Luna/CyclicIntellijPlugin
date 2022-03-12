package cyclic.intellij.refactoring.intentions;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractExprAction implements IntentionAction{
	
	public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file){
		return file instanceof CycFile && findAvailable(project, editor, (CycFile)file, editor.getCaretModel().getOffset()) != null;
	}
	
	public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException{
		invoke(project, editor, file, findAvailable(project, editor, (CycFile)file, editor.getCaretModel().getOffset()));
	}
	
	public boolean startInWriteAction(){
		return true;
	}
	
	private CycExpression findAvailable(@NotNull Project project, Editor editor, CycFile file, int position){
		return untilAvailable(project, editor, file, untilExpr(file.findElementAt(position)));
	}
	
	private CycExpression untilAvailable(@NotNull Project project, Editor editor, PsiFile file, CycExpression expr){
		if(expr == null)
			return null;
		if(isAvailable(project, editor, file, expr))
			return expr;
		if(expr.getParent() instanceof CycExpression)
			return untilAvailable(project, editor, file, (CycExpression)expr.getParent());
		return null;
	}
	
	private CycExpression untilExpr(PsiElement elem){
		if(elem == null)
			return null;
		if(elem instanceof CycExpression)
			return (CycExpression)elem;
		else
			return untilExpr(elem.getParent());
	}
	
	public abstract boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file, CycExpression expression);
	
	public abstract void invoke(@NotNull Project project, Editor editor, PsiFile file, CycExpression expression)
			throws IncorrectOperationException;
}
