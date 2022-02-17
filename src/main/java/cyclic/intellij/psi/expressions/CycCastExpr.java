package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import org.jetbrains.annotations.NotNull;

public class CycCastExpr extends CycExpression{
	
	public CycCastExpr(@NotNull ASTNode node){
		super(node);
	}
}