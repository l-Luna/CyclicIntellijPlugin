package cyclic.intellij.psi;

import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiElement;

public interface CycVariable extends PsiElement{
	
	String varName();
	
	JvmType varType();
	
	boolean hasModifier(String modifier);
}