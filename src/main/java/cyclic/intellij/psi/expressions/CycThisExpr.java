package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import org.jetbrains.annotations.NotNull;

public class CycThisExpr extends CycExpression{
	
	public CycThisExpr(@NotNull ASTNode node){
		super(node);
	}
}