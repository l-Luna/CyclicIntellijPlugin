package cyclic.intellij.psi.utils;

import com.intellij.psi.PsiElement;

// PsiClass/JvmClass are too complex to implement yet
public interface CPsiClass{
	
	PsiElement declaration();
	
	String fullyQualifiedName();
}