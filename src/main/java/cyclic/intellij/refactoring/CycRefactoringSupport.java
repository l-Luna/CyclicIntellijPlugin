package cyclic.intellij.refactoring;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.ast.statements.CycForeachLoop;
import org.jetbrains.annotations.NotNull;

public class CycRefactoringSupport extends RefactoringSupportProvider{
	
	public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context){
		return element instanceof CycForeachLoop;
	}
	
	public boolean isSafeDeleteAvailable(@NotNull PsiElement element){
		return element instanceof CycForeachLoop;
	}
}