package cyclic.intellij.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.types.CycClassList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface StubCycClassList<CL extends CycClassList<CL>> extends StubElement<CL>{
	
	@NotNull
	List<String> elementFqNames();
}