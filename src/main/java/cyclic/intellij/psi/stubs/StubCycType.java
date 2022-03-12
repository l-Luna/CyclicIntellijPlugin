package cyclic.intellij.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.CycKind;
import org.jetbrains.annotations.NotNull;

public interface StubCycType extends StubElement<CycType>{
	
	@NotNull
	String fullyQualifiedName();
	
	@NotNull
	String shortName();
	
	@NotNull
	CycKind kind();
}