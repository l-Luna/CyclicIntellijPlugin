package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.utils.CycIdHolder;
import cyclic.intellij.psi.utils.CPsiClass;
import cyclic.intellij.psi.utils.CycTypeReference;
import cyclic.intellij.psi.utils.JPsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycClassLiteralExpr extends CycExpression implements CycIdHolder{
	
	public CycClassLiteralExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable CPsiClass type(){
		return JPsiClass.of("java.lang.Class", getProject());
	}
	
	public PsiReference getReference(){
		return getIdElement().map(id -> new CycTypeReference(id, this)).orElse(null);
	}
}