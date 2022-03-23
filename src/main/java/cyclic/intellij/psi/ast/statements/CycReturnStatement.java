package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CycReturnStatement extends CycAstElement implements CycStatement{
	
	public CycReturnStatement(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycExpression> returns(){
		return PsiUtils.childOfType(this, CycExpression.class);
	}
	
	@Nullable("Null means no expression")
	public JvmType returnType(){
		return returns().map(CycExpression::type).orElse(null);
	}
}