package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import cyclic.intellij.psi.types.ArrayTypeImpl;
import cyclic.intellij.psi.types.ClassTypeImpl;
import cyclic.intellij.psi.utils.CycTypeReference;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class CycTypeRef extends CycElement{
	
	public CycTypeRef(@NotNull ASTNode node){
		super(node);
	}
	
	public JvmType asClass(){
		if(getNode().findChildByType(Tokens.SQ_BRACES) != null)
			return PsiUtils.childOfType(this, CycTypeRef.class)
					.map(CycTypeRef::asClass)
					.map(ArrayTypeImpl::of)
					.orElse(null);
		var ref = PsiUtils.childOfType(this, CycRawTypeRef.class)
				.map(CycRawTypeRef::getReference).orElse(null);
		if(ref instanceof CycTypeReference)
			return ClassTypeImpl.of(((CycTypeReference)ref).resolveClass());
		return null;
	}
}