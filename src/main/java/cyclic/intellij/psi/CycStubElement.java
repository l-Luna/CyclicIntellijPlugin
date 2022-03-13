package cyclic.intellij.psi;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

public class CycStubElement<Psi extends PsiElement, Stub extends StubElement<Psi>>
		extends StubBasedPsiElementBase<Stub>
		implements CycElement, StubBasedPsiElement<Stub>{
	
	private PsiElement navigationElement = null;
	
	public CycStubElement(@NotNull Stub stub, @NotNull IStubElementType<?, ?> nodeType){
		super(stub, nodeType);
	}
	
	public CycStubElement(@NotNull ASTNode node){
		super(node);
	}
	
	public CycStubElement<Psi, Stub> setNavigationElement(PsiElement navigationElement){
		this.navigationElement = navigationElement;
		return this;
	}
	
	public PsiElement getNavigationElement(){
		return navigationElement != null ? navigationElement : this;
	}
}