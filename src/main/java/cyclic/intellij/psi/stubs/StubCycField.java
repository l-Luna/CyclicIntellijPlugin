package cyclic.intellij.psi.stubs;

import cyclic.intellij.psi.ast.common.CycVariableDef;
import org.jetbrains.annotations.NotNull;

public interface StubCycField extends StubWithCycModifiers<CycVariableDef>, StubAsCycVariable<CycVariableDef>{
	
	@NotNull
	String name();
	
	@NotNull
	String typeText();
	
	@NotNull
	default String varName(){
		return name();
	}
	
	@NotNull
	default String varTypeText(){
		return typeText();
	}
	
	default boolean hasModifier(String modifier){
		var modList = modifiers();
		return modList != null && modList.modifiers().contains(modifier);
	}
}