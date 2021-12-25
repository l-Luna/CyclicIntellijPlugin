package cyclic.intellij.refactoring;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.CycDefinition;
import org.jetbrains.annotations.NotNull;

public class CycRefactoringSupport extends RefactoringSupportProvider{
	
	public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context){
		return element instanceof CycDefinition;
	}
}