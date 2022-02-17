package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import org.jetbrains.annotations.NotNull;

public class CycNewArrayExpr extends CycExpression{
	
	public CycNewArrayExpr(@NotNull ASTNode node){
		super(node);
	}
}