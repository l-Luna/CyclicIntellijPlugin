package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import cyclic.intellij.psi.types.ArrayTypeImpl;
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
		return PsiUtils.childOfType(this, CycRawTypeRef.class)
				.map(CycRawTypeRef::type).orElse(null);
	}
}