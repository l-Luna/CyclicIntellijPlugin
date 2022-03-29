package cyclic.intellij.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.inspections.fixes.ShortenReferenceFix;
import cyclic.intellij.psi.CycClassReference;
import cyclic.intellij.psi.ast.CycImportStatement;
import org.jetbrains.annotations.NotNull;

public class UnnecessaryQualifierInspection extends LocalInspectionTool{
	
	public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly){
		return new PsiElementVisitor(){
			public void visitElement(@NotNull PsiElement element){
				super.visitElement(element);
				var ref = element.getReference();
				if(!(element instanceof CycImportStatement) && ref instanceof CycClassReference){
					var cls = ((CycClassReference)ref);
					if(CycImportStatement.isQualificationRedundant(cls)){
						var shortName = cls.resolveClass().getName();
						// TODO: only highlight qualifier of reference
						holder.registerProblem(
								element,
								CyclicBundle.message("inspection.text.unnecessaryQualifier"),
								ProblemHighlightType.LIKE_UNUSED_SYMBOL,
								new ShortenReferenceFix(element, shortName));
					}
				}
			}
		};
	}
}