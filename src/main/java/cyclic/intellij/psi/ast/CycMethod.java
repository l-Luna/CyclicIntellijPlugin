package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.psi.CycDefinitionStubElement;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.stubs.StubCycMethod;
import cyclic.intellij.psi.stubs.StubTypes;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CycMethod extends CycDefinitionStubElement<CycMethod, StubCycMethod> implements CycModifiersHolder, CycVarScope{
	
	public CycMethod(@NotNull ASTNode node){
		super(node);
	}
	
	public CycMethod(@NotNull StubCycMethod method){
		super(method, StubTypes.CYC_METHOD);
	}
	
	public String fullyQualifiedName(){
		return containingType().fullyQualifiedName() + "::" + getName();
	}
	
	public CycType containingType(){
		return getStubOrPsiParentOfType(CycType.class);
	}
	
	public Optional<CycModifierList> modifiers(){
		var stub = getStub();
		if(stub != null)
			return Optional.ofNullable(stub.modifiers()).map(StubElement::getPsi);
		return PsiUtils.childOfType(this, CycModifierList.class);
	}
	
	public boolean isStatic(){
		return modifiers().map(x -> x.hasModifier("static")).orElse(false);
	}
	
	public Optional<CycTypeRef> returns(){
		var stub = getStub();
		if(stub != null){
			var type = PsiUtils.createTypeReferenceFromText(this, stub.returnTypeText());
			return Optional.of((CycTypeRef)type);
		}
		return PsiUtils.childOfType(this, CycTypeRef.class);
	}
	
	public List<CycParameter> parameters(){
		var stub = getStub();
		if(stub != null){
			var params = stub.parameters();
			return params.stream().map(StubElement::getPsi).collect(Collectors.toList());
		}
		var paramList = PsiUtils.childOfType(this, CycParametersList.class);
		return paramList.map(list -> PsiUtils.childrenOfType(list, CycParameter.class)).orElseGet(List::of);
	}
	
	public @Nullable JvmType returnType(){
		return returns().map(CycTypeRef::asType).orElse(PsiPrimitiveType.NULL);
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
		return JvmClassUtils.findMethodInHierarchy(JvmCyclicClass.of(containingType()), this::overrides, true);
	}
	
	public boolean hasSemicolon(){
		var stub = getStub();
		if(stub != null)
			return stub.hasSemicolon();
		
		var node = getNode().getLastChildNode().getFirstChildNode();
		return node != null && node.getElementType() == Tokens.getFor(CyclicLangLexer.SEMICOLON);
	}
	
	public String getName(){
		var stub = getStub();
		if(stub != null)
			return stub.name();
		
		return super.getName();
	}
}