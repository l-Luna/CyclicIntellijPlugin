package cyclic.intellij.psi.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.stubs.StubCycMethod;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StubImplCycMethod extends StubBase<CycMethod> implements StubCycMethod{
	
	@NotNull
	private final String name, returnType;
	private final boolean hasSemicolon;
	
	public StubImplCycMethod(@Nullable StubElement parent, String name, String returnType, boolean semicolon){
		super(parent, StubTypes.CYC_METHOD);
		this.name = name;
		this.returnType = returnType;
		hasSemicolon = semicolon;
	}
	
	public @NotNull String name(){
		return name;
	}
	
	public @NotNull String returnTypeText(){
		return returnType;
	}
	
	public boolean hasSemicolon(){
		return hasSemicolon;
	}
}
