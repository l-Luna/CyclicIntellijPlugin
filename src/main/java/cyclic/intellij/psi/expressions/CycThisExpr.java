package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.elements.CycExpression;
import cyclic.intellij.psi.elements.CycMethod;
import cyclic.intellij.psi.utils.JvmClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycThisExpr extends CycExpression{
	
	public CycThisExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable JvmType type(){
		var method = PsiTreeUtil.getParentOfType(this, CycMethod.class);
		if(method == null || method.isStatic())
			return null;
		return JvmClassUtils.asType(method.containingType());
	}
}