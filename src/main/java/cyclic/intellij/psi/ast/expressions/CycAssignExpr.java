package cyclic.intellij.psi.ast.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
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
	
	public @Nullable JvmType type(){
		return expression().map(CycExpression::type).orElse(null);
	}
}