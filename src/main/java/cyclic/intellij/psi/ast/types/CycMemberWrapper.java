package cyclic.intellij.psi.ast.types;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycStubElement;
import cyclic.intellij.psi.stubs.StubCycMemberWrapper;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.NotNull;

public class CycMemberWrapper extends CycStubElement<CycMemberWrapper, StubCycMemberWrapper>{
	
	public CycMemberWrapper(@NotNull ASTNode node){
		super(node);
	}
	
	public CycMemberWrapper(@NotNull StubCycMemberWrapper stub){
		super(stub, StubTypes.CYC_MEMBER);
	}
}