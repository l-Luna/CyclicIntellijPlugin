package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.CycCodeHolder;
import cyclic.intellij.psi.CycDefinitionAstElement;
import cyclic.intellij.psi.CycModifiersHolder;
import cyclic.intellij.psi.ast.common.CycParameter;
import cyclic.intellij.psi.ast.statements.CycStatement;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.JvmCyclicConstructor;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

// TODO: stubs
public class CycConstructor extends CycDefinitionAstElement implements CycModifiersHolder, CycCodeHolder{
	
	public CycConstructor(@NotNull ASTNode node){
		super(node);
	}
	
	public List<CycParameter> parameters(){
		var paramList = PsiUtils.childOfType(this, CycParametersList.class);
		return paramList.map(list -> PsiUtils.childrenOfType(list, CycParameter.class)).orElseGet(List::of);
	}
	
	public Optional<CycStatement> body(){
		// last child is either a block or (-> +) statement
		var last = getLastChild();
		if(last instanceof CycStatement)
			return Optional.of((CycStatement)last);
		return Optional.empty();
	}
	
	public JvmMethod toJvm(){
		return JvmCyclicConstructor.of(this);
	}
	
	public CycType containingType(){
		return PsiTreeUtil.getParentOfType(this, CycType.class);
	}
}
