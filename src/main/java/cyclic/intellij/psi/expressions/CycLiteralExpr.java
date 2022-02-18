package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.utils.CPsiClass;
import cyclic.intellij.psi.utils.PrimPsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycLiteralExpr extends CycExpression{
	
	public CycLiteralExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable CPsiClass type(){
		if(getNode().findChildByType(Tokens.TOK_NULL) != null)
			return PrimPsiClass.NULL;
		if(getNode().findChildByType(Tokens.TOK_BOOLLIT) != null)
			return PrimPsiClass.BOOLEAN;
		// TODO: implicit conversions
		if(getNode().findChildByType(Tokens.TOK_INTLIT) != null)
			return PrimPsiClass.INT;
		if(getNode().findChildByType(Tokens.TOK_DECLIT) != null)
			return PrimPsiClass.DOUBLE;
		return null;
	}
}