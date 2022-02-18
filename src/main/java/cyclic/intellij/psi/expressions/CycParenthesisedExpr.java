package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.utils.CPsiClass;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycParenthesisedExpr extends CycExpression{
	
	public CycParenthesisedExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable CPsiClass type(){
		var parenthesised = PsiUtils.childOfType(this, CycExpression.class);
		return parenthesised.map(CycExpression::type).orElse(null);
	}
}