package cyclic.intellij.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.psi.CycDefinition;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.utils.*;
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
		var paramList = PsiUtils.childOfType(this, CycParametersList.class);
		if(paramList.isPresent())
			return PsiUtils.childrenOfType(paramList.get(), CycParameter.class);
		return List.of();
	}
	
	public @Nullable JvmType returnType(){
		return returns().map(CycTypeRef::asType).orElse(null);
	}
	
	public List<? extends CycVariable> available(){
		return parameters();
	}
	
	public boolean isVarargs(){
		return parameters().stream().anyMatch(CycParameter::isVarargs);
	}
	
	public boolean overrides(JvmMethod other){
		if(getName().equals(other.getName()) && returnType() != null
				&& JvmClassUtils.isAssignableTo(returnType(), other.getReturnType())
				&& other.getParameters().length == parameters().size()){
			List<CycParameter> parameters = parameters();
			for(int i = 0; i < parameters.size(); i++){
				var type = parameters.get(i).varType();
				if(type == null || !type.equals(other.getParameters()[i].getType()))
					return false;
			}
			return true;
		}
		return false;
	}
	
	public @Nullable JvmMethod overriddenMethod(){
		// skip the containing class
		var fromSuper = JvmClassUtils.findMethodInSupertypes(containingType().getSuperType(), this::overrides);
		if(fromSuper != null)
			return fromSuper;
		for(JvmClass anInterface : containingType().getInterfaces()){
			var fromInterface = JvmClassUtils.findMethodInSupertypes(anInterface, this::overrides);
			if(fromInterface != null)
				return fromInterface;
		}
		return null;
	}
	
	public boolean hasSemicolon(){
		var node = getNode().getLastChildNode().getFirstChildNode();
		return node != null && node.getElementType() == Tokens.getFor(CyclicLangLexer.SEMICOLON);
	}
}