package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import cyclic.intellij.psi.utils.CycModifiersHolder;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;

public class CycVariableDef extends CycDefinition implements CycVariable, CycModifiersHolder{
	
	public CycVariableDef(@NotNull ASTNode node){
		super(node);
	}
	
	public String varName(){
		return getName();
	}
	
	public JvmType varType(){
		return PsiUtils.childOfType(this, CycTypeRef.class)
				.map(CycTypeRef::asType)
				// for var/val
				.orElseGet(() -> PsiUtils.childOfType(this, CycExpression.class).map(CycExpression::type).orElse(null));
	}
	
	public boolean hasModifier(String modifier){
		return CycModifiersHolder.super.hasModifier(modifier);
	}
	
	public boolean isLocalVar(){
		return PsiTreeUtil.getParentOfType(this, CycStatement.class) != null;
	}
	
	public Optional<CycExpression> initializer(){
		return PsiUtils.childOfType(this, CycExpression.class);
	}
	
	public boolean hasInferredType(){
		return PsiUtils.childOfType(this, CycTypeRef.class)
				.map(x -> x.getText().equals("var") || x.getText().equals("val"))
				.orElse(false);
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