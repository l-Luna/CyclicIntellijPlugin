package cyclic.intellij.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.utils.CycTypeReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RenameCycTypeProcessor extends RenamePsiElementProcessor{
	
	public boolean canProcessElement(@NotNull PsiElement element){
		return element instanceof CycType;
	}
	
	public void renameElement(@NotNull PsiElement element, @NotNull String newName, UsageInfo @NotNull [] usages, @Nullable RefactoringElementListener listener) throws IncorrectOperationException{
		// pass along the new fully qualified name instead
		// TODO: consider imports
		CycType type = (CycType)element;
		type.handleElementRename(newName);
		String fqNewName = type.fullyQualifiedName();
		for(UsageInfo usage : usages){
			PsiReference ref = usage.getReference();
			if(ref != null)
				if(ref instanceof CycTypeReference)
					ref.handleElementRename(fqNewName);
				else
					ref.handleElementRename(newName);
		}
	}
}