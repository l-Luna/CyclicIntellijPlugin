package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CycAssertStatement extends CycAstElement implements CycStatement{
	
	public CycAssertStatement(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycExpression> condition(){
		return PsiUtils.childOfType(this, CycExpression.class);
	}
}