package cyclic.intellij.inspections;

import com.intellij.codeInsight.daemon.impl.quickfix.DeleteElementFix;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.utils.Flow;
import org.jetbrains.annotations.NotNull;

public class UnreachableCodeInspection extends LocalInspectionTool{
	
	public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly){
		return new PsiElementVisitor(){
			public void visitElement(@NotNull PsiElement element){
				super.visitElement(element);
				if(element instanceof CycMethod){
					((CycMethod)element).body().ifPresent(body -> {
						// a warning, not error
						Flow.visitAfterMatching(body, Flow.EXITS, invalid ->
								holder.registerProblem(
										invalid.getParent(),
										CyclicBundle.message("inspection.text.unreachableCode"),
										ProblemHighlightType.LIKE_UNUSED_SYMBOL,
										new DeleteElementFix(invalid.getParent(),
												CyclicBundle.message("inspection.fix.text.deleteStatement"))));
					});
				}
			}
		};
	}
}