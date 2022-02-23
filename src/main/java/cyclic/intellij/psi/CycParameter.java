package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class CycParameter extends CycDefinition implements CycVariable{
	
	public CycParameter(@NotNull ASTNode node){
		super(node);
	}
	
	public String varName(){
		return getName();
	}
	
	public JvmType varType(){
		return PsiUtils.childOfType(this, CycTypeRef.class).map(CycTypeRef::asClass).orElse(null);
	}
	
	public @NotNull SearchScope getUseScope(){
		return new LocalSearchScope(getContainingFile());
	}
}