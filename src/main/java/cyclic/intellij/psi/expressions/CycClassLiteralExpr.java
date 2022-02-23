package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.types.ClassTypeImpl;
import cyclic.intellij.psi.utils.CycIdHolder;
import cyclic.intellij.psi.utils.CycTypeReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cyclic.intellij.psi.utils.JvmClassUtils.getByName;

public class CycClassLiteralExpr extends CycExpression implements CycIdHolder{
	
	public CycClassLiteralExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable JvmType type(){
		return getByName("java.lang.Class", getProject());
	}
	
	public PsiReference getReference(){
		return getIdElement().map(id -> new CycTypeReference(id, this)).orElse(null);
	}
}