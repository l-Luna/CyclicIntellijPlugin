package cyclic.intellij.psi.types;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CPsiMethod{
	
	@NotNull
	PsiElement declaration();
	
	@Nullable
	CPsiType returnType();
}