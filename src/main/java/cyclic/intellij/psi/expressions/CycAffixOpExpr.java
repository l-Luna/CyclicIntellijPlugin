package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.utils.CPsiClass;
import cyclic.intellij.psi.utils.PrimPsiClass;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CycAffixOpExpr extends CycExpression{
	
	public CycAffixOpExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public String operation(){
		if(isPostfix())
			return getLastChild().getText();
		else
			return getFirstChild().getText();
	}
	
	public boolean isPostfix(){
		return getFirstChild() instanceof CycExpression;
	}
	
	public Optional<CycExpression> expression(){
		return PsiUtils.childOfType(this, CycExpression.class);
	}
	
	public @Nullable CPsiClass type(){
		return expression().map(CycExpression::type).orElse(null);
	}
}