package cyclic.intellij.psi.utils;

import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.elements.CycModifierList;

import java.util.List;
import java.util.Optional;

public interface CycModifiersHolder extends PsiElement{
	
	default Optional<CycModifierList> getModifiersElement(){
		return PsiUtils.childOfType(this, CycModifierList.class);
	}
	
	default boolean hasModifier(String modifier){
		return getModifiersElement().map(k -> k.hasModifier(modifier)).orElse(false);
	}
	
	default List<String> getModifiers(){
		return getModifiersElement().map(CycModifierList::getModifiers).orElse(List.of());
	}
}