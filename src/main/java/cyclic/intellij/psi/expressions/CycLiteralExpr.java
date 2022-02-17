package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import org.jetbrains.annotations.NotNull;

public class CycLiteralExpr extends CycExpression{
	
	public CycLiteralExpr(@NotNull ASTNode node){
		super(node);
	}
}