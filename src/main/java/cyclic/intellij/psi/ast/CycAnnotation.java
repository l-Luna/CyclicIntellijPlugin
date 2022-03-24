package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.CycIdHolder;
import cyclic.intellij.psi.utils.CycTypeReference;
import org.jetbrains.annotations.NotNull;

public class CycAnnotation extends CycAstElement implements CycIdHolder{
	
	public CycAnnotation(@NotNull ASTNode node){
		super(node);
	}
	
	public PsiReference getReference(){
		return getIdElement().map(id -> new CycTypeReference(id, this)).orElse(null);
	}
}