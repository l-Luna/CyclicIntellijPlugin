package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.types.CPsiType;
import cyclic.intellij.psi.types.PrimPsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycLiteralExpr extends CycExpression{
	
	public CycLiteralExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable CPsiType type(){
		if(getNode().findChildByType(Tokens.TOK_NULL) != null)
			return PrimPsiType.NULL;
		if(getNode().findChildByType(Tokens.TOK_BOOLLIT) != null)
			return PrimPsiType.BOOLEAN;
		// TODO: implicit conversions
		if(getNode().findChildByType(Tokens.TOK_INTLIT) != null)
			return PrimPsiType.INT;
		if(getNode().findChildByType(Tokens.TOK_DECLIT) != null)
			return PrimPsiType.DOUBLE;
		return null;
	}
}