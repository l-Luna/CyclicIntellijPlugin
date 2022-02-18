package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CycMethod extends CycDefinition{
	
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
}