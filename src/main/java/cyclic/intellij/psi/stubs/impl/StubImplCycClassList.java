package cyclic.intellij.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.types.CycClassList;
import cyclic.intellij.psi.stubs.StubCycClassList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StubImplCycClassList<CL extends CycClassList<CL>> extends StubBase<CL> implements StubCycClassList<CL>{
	
	@NotNull
	private final List<String> elementFqNames;
	
	public StubImplCycClassList(@Nullable StubElement parent, IStubElementType elementType, @NotNull List<String> fqNames){
		super(parent, elementType);
		elementFqNames = fqNames;
	}
	
	public @NotNull List<String> elementFqNames(){
		return elementFqNames;
	}
}