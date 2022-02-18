package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.CycMethod;
import cyclic.intellij.psi.utils.CPsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycThisExpr extends CycExpression{
	
	public CycThisExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable CPsiClass type(){
		var method = PsiTreeUtil.getParentOfType(this, CycMethod.class);
		if(method == null || method.isStatic())
			return null;
		return method.containingType();
	}
}