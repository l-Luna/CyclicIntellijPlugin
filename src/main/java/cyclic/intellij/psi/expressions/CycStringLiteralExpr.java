package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.utils.CPsiClass;
import cyclic.intellij.psi.utils.JPsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycStringLiteralExpr extends CycExpression{
	
	public CycStringLiteralExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable CPsiClass type(){
		return JPsiClass.of("java.lang.String", getProject());
	}
}