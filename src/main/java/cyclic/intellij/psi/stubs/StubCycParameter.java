package cyclic.intellij.psi.stubs;

import cyclic.intellij.psi.ast.common.CycParameter;
import org.jetbrains.annotations.NotNull;

public interface StubCycParameter extends StubAsCycVariable<CycParameter>{
	
	@NotNull
	String name();
	
	@NotNull
	String typeText();
	
	boolean isVarargs();
	
	@NotNull
	default String varName(){
		return name();
	}
	
	@NotNull
	default String varTypeText(){
		return typeText();
	}
	
	default boolean hasModifier(String modifier){
		if(!modifier.equals("final"))
			return false;
		// method parameter finality is not externally visible
		return isRecordComponent();
	}
	
	default boolean isRecordComponent(){
		return getParentStub() instanceof StubCycRecordComponents;
	}
}