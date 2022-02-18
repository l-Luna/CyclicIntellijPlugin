package cyclic.intellij.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.CycMethod;
import cyclic.intellij.psi.expressions.CycThisExpr;
import org.jetbrains.annotations.NotNull;

public class ThisInStaticMethodInspection extends LocalInspectionTool{
	
	public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly){
		return new PsiElementVisitor(){
			public void visitElement(@NotNull PsiElement element){
				super.visitElement(element);
				if(element instanceof CycThisExpr){
					var container = PsiTreeUtil.getParentOfType(element, CycMethod.class);
					if(container != null && container.isStatic() && element.getText().equals("this"))
						holder.registerProblem(element, "'this' can only be used in non-static contexts", ProblemHighlightType.ERROR);
				}
			}
		};
	}
}