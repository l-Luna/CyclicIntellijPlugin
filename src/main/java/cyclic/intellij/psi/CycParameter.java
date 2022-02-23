package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.PlatformIcons;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CycParameter extends CycDefinition implements CycVariable{
	
	public CycParameter(@NotNull ASTNode node){
		super(node);
	}
	
	public String varName(){
		return getName();
	}
	
	public JvmType varType(){
		return PsiUtils.childOfType(this, CycTypeRef.class).map(CycTypeRef::asType).orElse(null);
	}
	
	public boolean isMethodParameter(){
		return !(getParent() instanceof CycRecordComponents);
	}
	
	public @NotNull SearchScope getUseScope(){
		return isMethodParameter() ? new LocalSearchScope(getContainingFile()) : super.getUseScope();
	}
	
	public @Nullable Icon getIcon(int flags){
		if(isMethodParameter())
			return PlatformIcons.PARAMETER_ICON;
		else
			return PlatformIcons.FIELD_ICON;
	}
}