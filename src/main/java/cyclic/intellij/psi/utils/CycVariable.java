package cyclic.intellij.psi.utils;

import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiElement;

public interface CycVariable extends PsiElement{
	
	String varName();
	
	JvmType varType();
	
	boolean hasModifier(String modifier);
}