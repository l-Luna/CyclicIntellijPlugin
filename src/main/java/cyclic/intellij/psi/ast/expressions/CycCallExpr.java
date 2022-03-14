package cyclic.intellij.psi.ast.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.lang.jvm.types.JvmType;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.ast.common.CycCall;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CycCallExpr extends CycExpression{
	
	public CycCallExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycCall> call(){
		return PsiUtils.childOfType(this, CycCall.class);
	}
	
	public Optional<CycExpression> on(){
		return PsiUtils.childOfType(this, CycExpression.class);
	}
	
	public boolean isSuperCall(){
		return !PsiUtils.matchingChildren(this, k -> k.getNode().getElementType() == Tokens.getFor(CyclicLangParser.SUPER)).isEmpty();
	}
	
	public @Nullable JvmType type(){
		return call().map(CycCall::resolveMethod).map(JvmMethod::getReturnType).orElse(null);
	}
}