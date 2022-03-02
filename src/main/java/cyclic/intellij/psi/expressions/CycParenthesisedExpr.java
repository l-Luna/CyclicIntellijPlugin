package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import cyclic.intellij.psi.elements.CycExpression;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycParenthesisedExpr extends CycExpression{
	
	public CycParenthesisedExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable JvmType type(){
		var parenthesised = PsiUtils.childOfType(this, CycExpression.class);
		return parenthesised.map(CycExpression::type).orElse(null);
	}
}