package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
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
		var defined = PsiUtils.wrappedChildrenOfType(this, CycVariableDef.class);
		var available = new ArrayList<CycVariable>(defined);
		CycVarScope.scopeOf(this).ifPresent(scope -> available.addAll(scope.available()));
		return available;
	}
}