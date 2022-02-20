package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.CycTypeRef;
import cyclic.intellij.psi.types.ArrayPsiType;
import cyclic.intellij.psi.types.CPsiType;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CycNewArrayExpr extends CycExpression{
	
	public CycNewArrayExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycTypeRef> elementType(){
		return PsiUtils.wrappedChildOfType(this, CycTypeRef.class);
	}
	
	public @Nullable CPsiType type(){
		return elementType().map(CycTypeRef::asClass).map(ArrayPsiType::of).orElse(null);
	}
}