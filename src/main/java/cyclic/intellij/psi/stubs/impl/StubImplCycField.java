package cyclic.intellij.psi.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.common.CycVariableDef;
import cyclic.intellij.psi.stubs.StubCycField;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StubImplCycField extends StubBase<CycVariableDef> implements StubCycField{
	
	@NotNull
	private final String name, typeText;
	
	public StubImplCycField(@Nullable StubElement parent, @NotNull String name, @NotNull String typeText){
		super(parent, StubTypes.CYC_FIELD);
		this.name = name;
		this.typeText = typeText;
	}
	
	public @NotNull String name(){
		return name;
	}
	
	public @NotNull String typeText(){
		return typeText;
	}
}
