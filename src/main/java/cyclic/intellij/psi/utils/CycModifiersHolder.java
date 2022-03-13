package cyclic.intellij.psi.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.CycStubElement;
import cyclic.intellij.psi.ast.CycModifierList;
import cyclic.intellij.psi.stubs.StubWithCycModifiers;

import java.util.List;
import java.util.Optional;

public interface CycModifiersHolder extends PsiElement{
	
	default Optional<CycModifierList> getModifiersElement(){
		if(this instanceof CycStubElement<?, ?>){
			var stub = ((CycStubElement<?, ?>)this).getStub();
			if(stub instanceof StubWithCycModifiers<?>)
				return Optional.ofNullable(((StubWithCycModifiers<?>)stub).modifiers()).map(StubElement::getPsi);
		}
		
		return PsiUtils.childOfType(this, CycModifierList.class);
	}
	
	default boolean hasModifier(String modifier){
		return getModifiersElement().map(k -> k.hasModifier(modifier)).orElse(false);
	}
	
	default List<String> getModifiers(){
		return getModifiersElement().map(CycModifierList::getModifiers).orElse(List.of());
	}
}