package cyclic.intellij.psi.utils;

import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.types.CPsiType;
import org.jetbrains.annotations.Nullable;

public interface CycVariable{
	
	String varName();
	
	CPsiType varType();
	
	@Nullable
	default PsiElement declaration(){
		if(this instanceof PsiElement)
			return (PsiElement)this;
		return null;
	}
}