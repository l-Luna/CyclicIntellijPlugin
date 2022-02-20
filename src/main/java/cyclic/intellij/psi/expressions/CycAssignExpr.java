package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.types.CPsiType;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CycAssignExpr extends CycExpression{
	
	public CycAssignExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycExpression> expression(){
		return PsiUtils.childOfType(this, CycExpression.class);
	}
	
	public @Nullable CPsiType type(){
		return expression().map(CycExpression::type).orElse(null);
	}
}