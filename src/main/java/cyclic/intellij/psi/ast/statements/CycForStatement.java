package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CycForStatement extends CycAstElement implements CycStatement{
	
	public CycForStatement(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycStatement> start(){
		return PsiUtils.childOfType(this, CycStatement.class, 0);
	}
	
	public Optional<CycExpression> condition(){
		return PsiUtils.childOfType(this, CycExpression.class, 0);
	}
	
	public Optional<CycStatement> updater(){
		return PsiUtils.childOfType(this, CycStatement.class, 1);
	}
	
	public Optional<CycStatement> body(){
		return PsiUtils.childOfType(this, CycStatement.class, 2);
	}
}