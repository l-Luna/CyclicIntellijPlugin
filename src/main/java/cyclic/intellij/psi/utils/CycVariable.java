package cyclic.intellij.psi.utils;

import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface CycVariable{
	
	String varName();
	
	JvmType varType();
	
	boolean hasModifier(String modifier);
	
	@Nullable
	default PsiElement declaration(){
		if(this instanceof PsiElement)
			return (PsiElement)this;
		return null;
	}
}