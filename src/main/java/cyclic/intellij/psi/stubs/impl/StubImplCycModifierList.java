package cyclic.intellij.psi.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.CycModifierList;
import cyclic.intellij.psi.stubs.StubCycModifierList;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StubImplCycModifierList extends StubBase<CycModifierList> implements StubCycModifierList{
	
	@NotNull
	private final List<String> modifiers;
	
	public StubImplCycModifierList(@Nullable StubElement parent, @NotNull List<String> modifiers){
		super(parent, StubTypes.CYC_MODIFIER_LIST);
		this.modifiers = modifiers;
	}
	
	public @NotNull List<String> modifiers(){
		return modifiers;
	}
}