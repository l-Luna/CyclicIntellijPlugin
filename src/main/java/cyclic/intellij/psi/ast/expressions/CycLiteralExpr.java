package cyclic.intellij.psi.ast.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiType;
import cyclic.intellij.psi.Tokens;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycLiteralExpr extends CycExpression{
	
	public CycLiteralExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable JvmType type(){
		if(getNode().findChildByType(Tokens.TOK_NULL) != null)
			return PsiType.NULL;
		if(getNode().findChildByType(Tokens.TOK_BOOLLIT) != null)
			return PsiType.BOOLEAN;
		// TODO: implicit conversions
		if(getNode().findChildByType(Tokens.TOK_INTLIT) != null)
			return PsiType.INT;
		if(getNode().findChildByType(Tokens.TOK_DECLIT) != null)
			return PsiType.DOUBLE;
		if(getNode().findChildByType(Tokens.TOK_CHARLIT) != null)
			return PsiType.CHAR;
		return null;
	}
}