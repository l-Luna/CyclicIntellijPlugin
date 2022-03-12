package cyclic.intellij.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import cyclic.intellij.psi.ast.types.CycClassList;
import org.jetbrains.annotations.NotNull;

public class CyclicInheritanceListIndex extends StringStubIndexExtension<CycClassList<?>>{
	
	public @NotNull StubIndexKey<String, CycClassList<?>> getKey(){
		return StubIndexes.INHERITANCE_LISTS;
	}
}