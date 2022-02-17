package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import org.jetbrains.annotations.NotNull;

public class CycCallExpr extends CycExpression{
	
	public CycCallExpr(@NotNull ASTNode node){
		super(node);
	}
}