package cyclic.intellij.psi.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import cyclic.intellij.psi.CycVariable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CyclicFieldIndex extends StringStubIndexExtension<CycVariable>{
	
	public @NotNull StubIndexKey<String, CycVariable> getKey(){
		return StubIndexes.FIELDS;
	}
	
	@Override
	public Collection<CycVariable> get(@NotNull final String name,
	                               @NotNull final Project project,
	                               @NotNull final GlobalSearchScope scope){
		return StubIndex.getElements(getKey(), name, project, null, CycVariable.class);
	}
}