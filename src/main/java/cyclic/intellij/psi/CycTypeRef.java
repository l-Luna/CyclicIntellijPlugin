package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.types.ArrayPsiType;
import cyclic.intellij.psi.types.CPsiType;
import cyclic.intellij.psi.utils.CycTypeReference;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class CycTypeRef extends CycElement{
	
	public CycTypeRef(@NotNull ASTNode node){
		super(node);
	}
	
	public CPsiType asClass(){
		if(getNode().findChildByType(Tokens.SQ_BRACES) != null){
			return PsiUtils.childOfType(this, CycTypeRef.class)
					.map(CycTypeRef::asClass)
					.map(ArrayPsiType::of)
					.orElse(null);
		}
		var ref = PsiUtils.childOfType(this, CycRawTypeRef.class)
				.map(CycRawTypeRef::getReference).orElse(null);
		if(ref instanceof CycTypeReference){
			CycTypeReference reference = (CycTypeReference)ref;
			return reference.resolveClass();
		}
		return null;
	}
}