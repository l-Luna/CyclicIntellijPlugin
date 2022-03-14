package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CycThrowStatement extends CycAstElement implements CycStatement{
	
	public CycThrowStatement(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycExpression> returns(){
		return PsiUtils.childOfType(this, CycExpression.class);
	}
}