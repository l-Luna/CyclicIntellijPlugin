package cyclic.intellij.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.types.CycExtendsClause;
import cyclic.intellij.psi.ast.types.CycImplementsClause;
import cyclic.intellij.psi.ast.types.CycPermitsClause;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.CycKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface StubCycType extends StubElement<CycType>{
	
	@NotNull
	String fullyQualifiedName();
	
	@NotNull
	String shortName();
	
	@NotNull
	CycKind kind();
	
	@Nullable
	default StubCycClassList<CycExtendsClause> extendsList(){
		return findChildStubByType(StubTypes.CYC_EXTENDS_LIST);
	}
	
	@Nullable
	default StubCycClassList<CycImplementsClause> implementsList(){
		return findChildStubByType(StubTypes.CYC_IMPLEMENTS_LIST);
	}
	
	@Nullable
	default StubCycClassList<CycPermitsClause> permitsList(){
		return findChildStubByType(StubTypes.CYC_PERMITS_LIST);
	}
}