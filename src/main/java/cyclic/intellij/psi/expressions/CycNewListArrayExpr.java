package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.CycTypeRef;
import cyclic.intellij.psi.types.ArrayTypeImpl;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CycNewListArrayExpr extends CycExpression{
	
	public CycNewListArrayExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycTypeRef> elementType(){
		return PsiUtils.childOfType(this, CycElement.class).flatMap(x -> PsiUtils.childOfType(x, CycTypeRef.class));
	}
	
	public @Nullable JvmType type(){
		return elementType().map(CycTypeRef::asClass).map(ArrayTypeImpl::of).orElse(null);
	}
}