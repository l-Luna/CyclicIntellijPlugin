package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.EmptyStub;
import cyclic.intellij.psi.CycStubElement;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.NotNull;

public class CycParametersList extends CycStubElement<CycParametersList, EmptyStub<CycParametersList>>{
	
	public CycParametersList(@NotNull ASTNode node){
		super(node);
	}
	
	public CycParametersList(@NotNull EmptyStub<CycParametersList> stub){
		super(stub, StubTypes.CYC_PARAMETERS_LIST);
	}
}