package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.CycCall;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.types.CPsiType;
import cyclic.intellij.psi.types.CPsiMethod;
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
	
	public @Nullable CPsiType type(){
		return call().map(x -> x.resolve(on().orElse(null))).map(CPsiMethod::returnType).orElse(null);
	}
}