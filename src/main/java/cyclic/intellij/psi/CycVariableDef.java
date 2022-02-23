package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CycVariableDef extends CycDefinition implements CycVariable{
	
	public CycVariableDef(@NotNull ASTNode node){
		super(node);
	}
	
	public String varName(){
		return getName();
	}
	
	public JvmType varType(){
		return PsiUtils.childOfType(this, CycTypeRef.class)
				.map(CycTypeRef::asClass)
				// for var/val
				.orElseGet(() -> PsiUtils.childOfType(this, CycExpression.class).map(CycExpression::type).orElse(null));
	}
	
	public boolean isLocalVar(){
		return PsiTreeUtil.getParentOfType(this, CycStatement.class) != null;
	}
	
	public @NotNull SearchScope getUseScope(){
		return isLocalVar() ? new LocalSearchScope(getContainingFile()) : super.getUseScope();
	}
	
	public @Nullable Icon getIcon(int flags){
		if(isLocalVar())
			return PlatformIcons.VARIABLE_ICON;
		else
			return PlatformIcons.FIELD_ICON;
	}
}