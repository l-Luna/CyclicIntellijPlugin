package cyclic.intellij.psi.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.common.CycParameter;
import cyclic.intellij.psi.stubs.StubCycParameter;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StubImplCycParameter extends StubBase<CycParameter> implements StubCycParameter{
	
	@NotNull
	private final String name, typeText;
	private final boolean varargs;
	
	public StubImplCycParameter(@Nullable StubElement parent,
	                            @NotNull String name,
	                            @NotNull String typeText,
	                            boolean varargs){
		super(parent, StubTypes.CYC_PARAMETER);
		this.name = name;
		this.typeText = typeText;
		this.varargs = varargs;
	}
	
	@NotNull
	public String name(){
		return name;
	}
	
	public @NotNull String typeText(){
		return typeText;
	}
	
	public boolean isVarargs(){
		return varargs;
	}
}
