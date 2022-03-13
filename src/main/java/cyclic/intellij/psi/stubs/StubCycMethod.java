package cyclic.intellij.psi.stubs;

import com.intellij.psi.stubs.EmptyStub;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.CycMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface StubCycMethod extends StubWithCycModifiers<CycMethod>{
	
	@NotNull
	String name();
	
	@NotNull
	default List<StubCycParameter> parameters(){
		return getChildrenStubs().stream()
				.filter(EmptyStub.class::isInstance)
				.flatMap(x -> (Stream<StubElement<?>>)x.getChildrenStubs().stream())
				.filter(StubCycParameter.class::isInstance)
				.map(StubCycParameter.class::cast)
				.collect(Collectors.toList());
	}
	
	@NotNull
	String returnTypeText();
	
	boolean hasSemicolon();
}