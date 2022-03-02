package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import cyclic.intellij.psi.elements.CycExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cyclic.intellij.psi.utils.JvmClassUtils.getByName;

public class CycStringLiteralExpr extends CycExpression{
	
	public CycStringLiteralExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable JvmType type(){
		return getByName("java.lang.String", getProject());
	}
}