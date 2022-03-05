package cyclic.intellij.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.utils.CycIdHolder;
import cyclic.intellij.psi.utils.CycTypeReference;
import org.jetbrains.annotations.NotNull;

public class CycAnnotation extends CycElement implements CycIdHolder{
	
	public CycAnnotation(@NotNull ASTNode node){
		super(node);
	}
	
	public PsiReference getReference(){
		return getIdElement().map(id -> new CycTypeReference(id, this)).orElse(null);
	}
}