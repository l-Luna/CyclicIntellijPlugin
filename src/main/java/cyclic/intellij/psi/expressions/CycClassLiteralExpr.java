package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.utils.CycIdHolder;
import cyclic.intellij.psi.types.CPsiType;
import cyclic.intellij.psi.utils.CycTypeReference;
import cyclic.intellij.psi.types.JPsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycClassLiteralExpr extends CycExpression implements CycIdHolder{
	
	public CycClassLiteralExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable CPsiType type(){
		return JPsiType.of("java.lang.Class", getProject());
	}
	
	public PsiReference getReference(){
		return getIdElement().map(id -> new CycTypeReference(id, this)).orElse(null);
	}
}