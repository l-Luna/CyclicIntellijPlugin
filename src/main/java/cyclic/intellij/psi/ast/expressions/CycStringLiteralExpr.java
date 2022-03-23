package cyclic.intellij.psi.ast.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cyclic.intellij.psi.utils.JvmClassUtils.typeByName;

public class CycStringLiteralExpr extends CycExpression{
	
	public CycStringLiteralExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable JvmType type(){
		return typeByName("java.lang.String", getProject());
	}
}