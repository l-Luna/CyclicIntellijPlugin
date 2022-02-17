package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import org.jetbrains.annotations.NotNull;

public class CycInstanceOfExpr extends CycExpression{
	
	public CycInstanceOfExpr(@NotNull ASTNode node){
		super(node);
	}
}