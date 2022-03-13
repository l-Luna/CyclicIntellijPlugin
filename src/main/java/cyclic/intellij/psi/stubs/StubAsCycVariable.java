package cyclic.intellij.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.utils.CycVariable;
import org.jetbrains.annotations.NotNull;

public interface StubAsCycVariable<Psi extends CycVariable> extends StubElement<Psi>{
	
	@NotNull
	String varName();
	
	@NotNull
	String varTypeText();
	
	boolean hasModifier(String modifier);
}