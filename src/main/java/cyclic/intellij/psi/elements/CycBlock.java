package cyclic.intellij.psi.elements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.utils.CycVarScope;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CycBlock extends CycElement implements CycVarScope{
	
	public CycBlock(@NotNull ASTNode node){
		super(node);
	}
	
	public List<? extends CycVariable> available(){
		// all wrapped CycVariableDefs
		// plus our super-scope's variable
		var defined = PsiUtils.wrappedChildrenOfType(this, CycVariable.class);
		var available = new ArrayList<>(defined);
		CycVarScope.scopeOf(this).ifPresent(scope -> available.addAll(scope.available()));
		return available;
	}
}