package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

public class CycDefinitionStubElement<Psi extends PsiElement, Stub extends StubElement<Psi>>
		extends CycStubElement<Psi, Stub>
		implements CycDefinition{
	
	public CycDefinitionStubElement(@NotNull ASTNode node){
		super(node);
	}
	
	public CycDefinitionStubElement(@NotNull Stub stub, @NotNull IStubElementType<?, ?> nodeType){
		super(stub, nodeType);
	}
	
	@NotNull
	public String getName(){
		return CycDefinition.super.getName();
	}
	
	public int getTextOffset(){
		return CycDefinition.super.getTextOffset();
	}
	
	public PsiReference getReference(){
		return CycDefinition.super.getReference();
	}
}