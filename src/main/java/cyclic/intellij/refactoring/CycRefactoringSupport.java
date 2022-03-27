package cyclic.intellij.refactoring;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.CycDefinition;
import cyclic.intellij.psi.CycVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycRefactoringSupport extends RefactoringSupportProvider{
	
	public boolean isSafeDeleteAvailable(@NotNull PsiElement element){
		return element instanceof CycDefinition;
	}
	
	public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context){
		return element instanceof CycVariable && ((CycVariable)element).isLocal();
	}
	
	public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, @Nullable PsiElement context){
		return element instanceof CycDefinition && !(element instanceof CycVariable && ((CycVariable)element).isLocal());
	}
}