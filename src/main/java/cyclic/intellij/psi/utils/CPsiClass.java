package cyclic.intellij.psi.utils;

import com.intellij.psi.PsiElement;

// PsiClass/JvmClass are too complex to implement at the moment
public interface CPsiClass{
	
	PsiElement declaration();
	
	String fullyQualifiedName();
}