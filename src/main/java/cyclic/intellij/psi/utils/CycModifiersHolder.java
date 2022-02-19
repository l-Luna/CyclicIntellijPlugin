package cyclic.intellij.psi.utils;

import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.CycId;
import cyclic.intellij.psi.CycModifierList;

import java.util.Optional;

public interface CycModifiersHolder extends PsiElement{
	
	default Optional<CycModifierList> getModifiersElement(){
		return PsiUtils.childOfType(this, CycModifierList.class);
	}
	
	default boolean hasModifier(String modifier){
		return getModifiersElement().map(k -> k.hasModifier(modifier)).orElse(false);
	}
}