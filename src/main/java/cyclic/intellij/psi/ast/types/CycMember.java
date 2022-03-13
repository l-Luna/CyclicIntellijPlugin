package cyclic.intellij.psi.ast.types;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycStubElement;
import cyclic.intellij.psi.stubs.StubCycMember;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.NotNull;

public class CycMember extends CycStubElement<CycMember, StubCycMember>{
	
	public CycMember(@NotNull ASTNode node){
		super(node);
	}
	
	public CycMember(@NotNull StubCycMember stub){
		super(stub, StubTypes.CYC_MEMBER);
	}
}