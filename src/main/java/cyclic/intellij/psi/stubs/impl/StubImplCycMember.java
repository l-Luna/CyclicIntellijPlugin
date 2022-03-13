package cyclic.intellij.psi.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.types.CycMember;
import cyclic.intellij.psi.stubs.StubCycMember;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.Nullable;

public class StubImplCycMember extends StubBase<CycMember> implements StubCycMember{
	
	public StubImplCycMember(@Nullable StubElement parent){
		super(parent, StubTypes.CYC_MEMBER);
	}
}