package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiType;
import cyclic.intellij.psi.elements.CycExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycInstanceOfExpr extends CycExpression{
	
	public CycInstanceOfExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable JvmType type(){
		return PsiType.BOOLEAN;
	}
}