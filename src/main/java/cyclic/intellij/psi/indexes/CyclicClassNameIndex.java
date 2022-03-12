package cyclic.intellij.psi.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import cyclic.intellij.psi.ast.types.CycType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CyclicClassNameIndex extends StringStubIndexExtension<CycType>{
	
	public @NotNull StubIndexKey<String, CycType> getKey(){
		return StubIndexes.TYPES_BY_FQ_NAME;
	}
	
	@Override
	public Collection<CycType> get(@NotNull final String fqName,
	                               @NotNull final Project project,
	                               @NotNull final GlobalSearchScope scope){
		return StubIndex.getElements(getKey(), fqName, project, null, CycType.class);
	}
}