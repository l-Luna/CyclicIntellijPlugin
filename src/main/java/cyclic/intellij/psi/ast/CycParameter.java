package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.PlatformIcons;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.psi.CycDefinitionStubElement;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.ast.types.CycRecordComponents;
import cyclic.intellij.psi.stubs.StubCycParameter;
import cyclic.intellij.psi.stubs.StubTypes;
import cyclic.intellij.psi.types.ArrayTypeImpl;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;

public class CycParameter extends CycDefinitionStubElement<CycParameter, StubCycParameter> implements CycVariable{
	
	public CycParameter(@NotNull ASTNode node){
		super(node);
	}
	
	public CycParameter(@NotNull StubCycParameter parameter){
		super(parameter, StubTypes.CYC_PARAMETER);
	}
	
	public String varName(){
		return getName();
	}
	
	public String getName(){
		var stub = getStub();
		if(stub != null)
			return stub.name();
		return super.getName();
	}
	
	public JvmType varType(){
		var type = getTypeName()
				.map(CycTypeRef::asType)
				.orElse(PsiType.NULL);
		return isVarargs() ? ArrayTypeImpl.of(type) : type;
	}
	
	public boolean hasModifier(String modifier){
		var stub = getStub();
		if(stub != null)
			return stub.hasModifier(modifier);
		
		if(!modifier.equals("final"))
			return false;
		return getNode().findChildByType(Tokens.getFor(CyclicLangLexer.FINAL)) != null;
	}
	
	@NotNull
	public Optional<CycTypeRef> getTypeName(){
		var stub = getStub();
		if(stub != null){
			var type = PsiUtils.createTypeReferenceFromText(this, stub.typeText());
			return Optional.of((CycTypeRef)type);
		}
		
		return PsiUtils.childOfType(this, CycTypeRef.class);
	}
	
	public boolean isMethodParameter(){
		var stub = getStub();
		if(stub != null)
			return !stub.isRecordComponent();
		
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
		var stub = getStub();
		if(stub != null)
			return stub.isVarargs();
		
		return getNode().findChildByType(Tokens.getFor(CyclicLangLexer.ELIPSES)) != null;
	}
}