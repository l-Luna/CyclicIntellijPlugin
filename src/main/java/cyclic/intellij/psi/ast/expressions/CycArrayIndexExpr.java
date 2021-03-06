package cyclic.intellij.psi.ast.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmArrayType;
import com.intellij.lang.jvm.types.JvmType;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CycArrayIndexExpr extends CycExpression{
	
	public CycArrayIndexExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycExpression> arrayExpr(){
		return PsiUtils.childOfType(this, CycExpression.class, 0);
	}
	
	public Optional<CycExpression> indexExpr(){
		return PsiUtils.childOfType(this, CycExpression.class, 1);
	}
	
	public @Nullable JvmType type(){
		return arrayExpr()
				.map(CycExpression::type)
				.map(x -> x instanceof JvmArrayType ? x : null)
				.map(x-> ((JvmArrayType)x).getComponentType())
				.orElse(null);
	}
}