package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CycTypeRefOrInferred extends CycAstElement{
	
	public CycTypeRefOrInferred(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycTypeRef> ref(){
		return PsiUtils.childOfType(this, CycTypeRef.class);
	}
}
