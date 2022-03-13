package cyclic.intellij.psi.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import cyclic.intellij.psi.ast.CycMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CyclicMethodIndex extends StringStubIndexExtension<CycMethod>{
	
	public @NotNull StubIndexKey<String, CycMethod> getKey(){
		return StubIndexes.METHODS;
	}
	
	@Override
	public Collection<CycMethod> get(@NotNull final String name,
	                               @NotNull final Project project,
	                               @NotNull final GlobalSearchScope scope){
		return StubIndex.getElements(getKey(), name, project, null, CycMethod.class);
	}
}