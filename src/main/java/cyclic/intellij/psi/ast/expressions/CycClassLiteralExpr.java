package cyclic.intellij.psi.ast.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiReference;
import cyclic.intellij.psi.CycIdHolder;
import cyclic.intellij.psi.utils.CycTypeReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cyclic.intellij.psi.utils.JvmClassUtils.typeByName;

public class CycClassLiteralExpr extends CycExpression implements CycIdHolder{
	
	public CycClassLiteralExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable JvmType type(){
		return typeByName("java.lang.Class", getProject());
	}
	
	public PsiReference getReference(){
		return getIdElement().map(id -> new CycTypeReference(id, this)).orElse(null);
	}
}