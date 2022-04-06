package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.CycVarScope;
import cyclic.intellij.psi.CycVariable;
import cyclic.intellij.psi.ast.common.CycVariableDef;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CycForStatement extends CycAstElement implements CycStatement, CycVarScope{
	
	public CycForStatement(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycStatementWrapper> start(){
		return PsiUtils.childOfType(this, CycStatementWrapper.class, 0);
	}
	
	public Optional<CycExpression> condition(){
		return PsiUtils.childOfType(this, CycExpression.class, 0);
	}
	
	public Optional<CycStatementWrapper> updater(){
		return PsiUtils.childOfType(this, CycStatementWrapper.class, 1);
	}
	
	public Optional<CycStatementWrapper> body(){
		return PsiUtils.childOfType(this, CycStatementWrapper.class, 2);
	}
	
	public List<? extends CycVariable> available(){
		List<CycVariable> superScope = new ArrayList<>(CycVarScope.scopeOf(this).map(CycVarScope::available).orElse(List.of()));
		// add the index variable (or whatever)
		start().flatMap(CycStatementWrapper::inner).ifPresent(s -> {
			if(s instanceof CycVariableDef)
				superScope.add((CycVariable)s);
		});
		return superScope;
	}
}