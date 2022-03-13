package cyclic.intellij.psi.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.types.CycRecordComponents;
import cyclic.intellij.psi.stubs.StubCycRecordComponents;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.Nullable;

public class StubImplCycRecordComponents extends StubBase<CycRecordComponents> implements StubCycRecordComponents{
	
	public StubImplCycRecordComponents(@Nullable StubElement parent){
		super(parent, StubTypes.CYC_RECORD_COMPONENTS);
	}
}