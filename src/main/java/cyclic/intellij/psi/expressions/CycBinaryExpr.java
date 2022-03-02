package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.elements.CycBinaryOp;
import cyclic.intellij.psi.elements.CycExpression;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CycBinaryExpr extends CycExpression{
	
	public CycBinaryExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycBinaryOp> op(){
		return PsiUtils.childOfType(this, CycBinaryOp.class);
	}
	
	public String symbol(){
		return op().map(PsiElement::getText).orElse("");
	}
	
	public Optional<CycExpression> left(){
		return PsiUtils.childOfType(this, CycExpression.class, 0);
	}
	
	public Optional<CycExpression> right(){
		return PsiUtils.childOfType(this, CycExpression.class, 1);
	}
}