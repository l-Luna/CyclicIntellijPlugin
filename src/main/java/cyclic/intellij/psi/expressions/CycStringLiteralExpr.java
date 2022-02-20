package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.types.CPsiType;
import cyclic.intellij.psi.types.JPsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycStringLiteralExpr extends CycExpression{
	
	public CycStringLiteralExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable CPsiType type(){
		return JPsiType.of("java.lang.String", getProject());
	}
}