package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.utils.CycModifiersHolder;
import cyclic.intellij.psi.utils.CycVarScope;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CycMethod extends CycDefinition implements CycModifiersHolder, CycVarScope{
	
	public CycMethod(@NotNull ASTNode node){
		super(node);
	}
	
	public CycType getType(){
		// parent is CycMember, then CycType
		return (CycType)getParent().getParent();
	}
	
	public String fullyQualifiedName(){
		return getType().fullyQualifiedName() + "::" + getName();
	}
	
	public CycType containingType(){
		return PsiTreeUtil.getParentOfType(this, CycType.class);
	}
	
	public Optional<CycModifierList> modifiers(){
		return PsiUtils.childOfType(this, CycModifierList.class);
	}
	
	public boolean isStatic(){
		return modifiers().map(x -> x.hasModifier("static")).orElse(false);
	}
	
	public Optional<CycTypeRef> returns(){
		return PsiUtils.childOfType(this, CycTypeRef.class);
	}
	
	public List<CycParameter> parameters(){
		return PsiUtils.wrappedChildrenOfType(this, CycParameter.class);
	}
	
	public @Nullable JvmType returnType(){
		return returns().map(CycTypeRef::asType).orElse(null);
	}
	
	public List<? extends CycVariable> available(){
		return parameters();
	}
}