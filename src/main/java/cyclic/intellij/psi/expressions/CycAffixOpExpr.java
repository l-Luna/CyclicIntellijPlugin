package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import cyclic.intellij.psi.elements.CycExpression;
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
	
	public @Nullable JvmType type(){
		return expression().map(CycExpression::type).orElse(null);
	}
}