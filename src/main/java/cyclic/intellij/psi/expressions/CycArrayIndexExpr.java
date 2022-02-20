package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.types.ArrayPsiType;
import cyclic.intellij.psi.types.CPsiType;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CycArrayIndexExpr extends CycExpression{
	
	public CycArrayIndexExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycExpression> arrayExpr(){
		return PsiUtils.childOfType(this, CycExpression.class, 0);
	}
	
	public Optional<CycExpression> indexExpr(){
		return PsiUtils.childOfType(this, CycExpression.class, 1);
	}
	
	public @Nullable CPsiType type(){
		return arrayExpr()
				.map(CycExpression::type)
				.map(x -> x instanceof ArrayPsiType ? x : null)
				.map(x-> ((ArrayPsiType)x).getElement())
				.orElse(null);
	}
}