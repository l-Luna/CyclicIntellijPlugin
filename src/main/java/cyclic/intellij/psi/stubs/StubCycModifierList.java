package cyclic.intellij.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.CycModifierList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface StubCycModifierList extends StubElement<CycModifierList>{
	
	@NotNull
	List<String> modifiers();
}