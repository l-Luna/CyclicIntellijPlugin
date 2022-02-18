package cyclic.intellij.psi.utils;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// PsiClass/JvmClass are too complex to implement at the moment
public interface CPsiClass{
	
	@Nullable
	PsiElement declaration();
	
	@NotNull
	String fullyQualifiedName();
}