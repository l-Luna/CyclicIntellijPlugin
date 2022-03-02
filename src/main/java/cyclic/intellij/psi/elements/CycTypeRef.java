package cyclic.intellij.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.types.JvmType;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.types.ArrayTypeImpl;
import cyclic.intellij.psi.utils.JvmClassUtils;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class CycTypeRef extends CycElement{
	
	public CycTypeRef(@NotNull ASTNode node){
		super(node);
	}
	
	public JvmType asType(){
		if(getNode().findChildByType(Tokens.SQ_BRACES) != null)
			return PsiUtils.childOfType(this, CycTypeRef.class)
					.map(CycTypeRef::asType)
					.map(ArrayTypeImpl::of)
					.orElse(null);
		return PsiUtils.childOfType(this, CycRawTypeRef.class)
				.map(CycRawTypeRef::type).orElse(null);
	}
	
	public JvmClass asClass(){
		return JvmClassUtils.asClass(asType());
	}
}