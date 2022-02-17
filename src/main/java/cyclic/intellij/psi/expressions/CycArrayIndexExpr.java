package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import org.jetbrains.annotations.NotNull;

public class CycArrayIndexExpr extends CycExpression{
	
	public CycArrayIndexExpr(@NotNull ASTNode node){
		super(node);
	}
}