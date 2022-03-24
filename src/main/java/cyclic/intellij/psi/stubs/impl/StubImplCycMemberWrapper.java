package cyclic.intellij.psi.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.types.CycMemberWrapper;
import cyclic.intellij.psi.stubs.StubCycMemberWrapper;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.Nullable;

public class StubImplCycMemberWrapper extends StubBase<CycMemberWrapper> implements StubCycMemberWrapper{
	
	public StubImplCycMemberWrapper(@Nullable StubElement parent){
		super(parent, StubTypes.CYC_MEMBER);
	}
}