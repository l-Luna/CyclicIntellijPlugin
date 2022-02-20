package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.types.CPsiType;
import cyclic.intellij.psi.types.PrimPsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycInstanceOfExpr extends CycExpression{
	
	public CycInstanceOfExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable CPsiType type(){
		return PrimPsiType.BOOLEAN;
	}
}