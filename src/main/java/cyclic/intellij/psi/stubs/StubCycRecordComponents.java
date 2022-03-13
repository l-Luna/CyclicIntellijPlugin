package cyclic.intellij.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.types.CycRecordComponents;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public interface StubCycRecordComponents extends StubElement<CycRecordComponents>{
	
	@NotNull
	default List<StubCycParameter> components(){
		return getChildrenStubs().stream()
				.filter(StubCycParameter.class::isInstance)
				.map(StubCycParameter.class::cast)
				.collect(Collectors.toList());
	}
}