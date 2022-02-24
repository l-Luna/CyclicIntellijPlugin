package cyclic.intellij.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import cyclic.intellij.psi.utils.CycTypeReference;
import org.jetbrains.annotations.NotNull;

public class InvalidTypeRefInspection extends LocalInspectionTool{
	
	public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly){
		return new PsiElementVisitor(){
			public void visitElement(@NotNull PsiElement element){
				super.visitElement(element);
				var ref = element.getReference();
				if(ref instanceof CycTypeReference)
					if(ref.resolve() == null){
						var text = element.getText();
						if(text.length() > 0 && !(text.equals("boolean") || text.equals("byte") || text.equals("short") || text.equals("char")
							|| text.equals("int") || text.equals("long") || text.equals("float") || text.equals("double")))
							holder.registerProblem(ref);
					}
			}
		};
	}
}