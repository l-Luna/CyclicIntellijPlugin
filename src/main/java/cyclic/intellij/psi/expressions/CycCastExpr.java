package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.CycTypeRef;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CycCastExpr extends CycExpression{
	
	public CycCastExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycTypeRef> castingTo(){
		return PsiUtils.wrappedChildOfType(this, CycTypeRef.class);
	}
	
	public @Nullable JvmType type(){
		return castingTo().map(CycTypeRef::asType).orElse(null);
	}
}