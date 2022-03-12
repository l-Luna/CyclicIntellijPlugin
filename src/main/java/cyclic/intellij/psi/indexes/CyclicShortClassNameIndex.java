package cyclic.intellij.psi.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import cyclic.intellij.psi.ast.types.CycType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CyclicShortClassNameIndex extends StringStubIndexExtension<CycType>{
	
	public @NotNull StubIndexKey<String, CycType> getKey(){
		return StubIndexes.TYPES_BY_SHORT_NAME;
	}
	
	@Override
	public Collection<CycType> get(@NotNull final String shortName,
	                               @NotNull final Project project,
	                               @NotNull final GlobalSearchScope scope){
		return StubIndex.getElements(getKey(), shortName, project, null, CycType.class);
	}
}