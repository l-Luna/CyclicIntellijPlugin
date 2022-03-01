package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.PlatformIcons;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.psi.types.ArrayTypeImpl;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;

public class CycParameter extends CycDefinition implements CycVariable{
	
	public CycParameter(@NotNull ASTNode node){
		super(node);
	}
	
	public String varName(){
		return getName();
	}
	
	public JvmType varType(){
		// method resolution and main class finding expects this
		var type = getTypeName()
				.map(CycTypeRef::asType)
				.orElse(PsiType.NULL);
		return isVarargs() ? ArrayTypeImpl.of(type) : type;
	}
	
	public boolean hasModifier(String modifier){
		if(!modifier.equals("final"))
			return false;
		return getNode().findChildByType(Tokens.getFor(CyclicLangLexer.FINAL)) != null;
	}
	
	@NotNull
	public Optional<CycTypeRef> getTypeName(){
		return PsiUtils.childOfType(this, CycTypeRef.class);
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
	
	public boolean isVarargs(){
		return getNode().findChildByType(Tokens.getFor(CyclicLangLexer.ELIPSES)) != null;
	}
}