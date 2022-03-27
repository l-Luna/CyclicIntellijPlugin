package cyclic.intellij.psi.ast.common;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import cyclic.intellij.psi.CycDefinitionStubElement;
import cyclic.intellij.psi.CycModifiersHolder;
import cyclic.intellij.psi.CycVariable;
import cyclic.intellij.psi.ast.CycTypeRef;
import cyclic.intellij.psi.ast.CycTypeRefOrInferred;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.ast.statements.CycStatement;
import cyclic.intellij.psi.ast.statements.CycStatementWrapper;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.stubs.StubCycField;
import cyclic.intellij.psi.stubs.StubTypes;
import cyclic.intellij.psi.types.ClassTypeImpl;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;

public class CycVariableDef extends CycDefinitionStubElement<CycVariableDef, StubCycField>
		implements CycVariable, CycModifiersHolder, CycStatement{
	
	public CycVariableDef(@NotNull ASTNode node){
		super(node);
	}
	
	public CycVariableDef(@NotNull StubCycField field){
		super(field, StubTypes.CYC_FIELD);
	}
	
	public String varName(){
		return getName();
	}
	
	public JvmType varType(){
		var stub = getStub();
		if(stub != null){
			// val is allowed here for enum fields only
			String text = stub.typeText();
			CycType container = stub.getParentStubOfType(CycType.class);
			if(text.equals("val") && container != null)
				return ClassTypeImpl.of(container);
			var type = PsiUtils.createTypeReferenceFromText(this, text);
			return Optional.of((CycTypeRef)type)
					.map(CycTypeRef::asType)
					.orElse(PsiPrimitiveType.NULL);
		}
		
		return PsiUtils.childOfType(this, CycTypeRefOrInferred.class)
				.flatMap(CycTypeRefOrInferred::ref)
				.map(CycTypeRef::asType)
				// for var/val
				.orElseGet(() -> {
					if(!isLocal()){
						if(PsiUtils.childOfType(this, CycTypeRefOrInferred.class).map(PsiElement::getText).orElse("").equals("val"))
							return ClassTypeImpl.of(PsiTreeUtil.getParentOfType(this, CycType.class));
						return PsiPrimitiveType.NULL;
					}
					return PsiUtils.childOfType(this, CycExpression.class)
							.map(CycExpression::type)
							.orElse(PsiPrimitiveType.NULL);
				});
	}
	
	public boolean hasModifier(String modifier){
		if(modifier.equals("final") || modifier.equals("static") || modifier.equals("public")){
			var stub = getStub(); // only fields have stubs
			if(stub != null){
				if(stub.typeText().equals("val"))
					return true;
			}else{
				var type = PsiUtils.childOfType(this, CycTypeRefOrInferred.class);
				if(!isLocal() && type.isPresent() && type.get().getText().equals("val"))
					return true;
			}
		}
		return CycModifiersHolder.super.hasModifier(modifier);
	}
	
	public boolean isLocal(){
		if(getStub() != null)
			return false;
		return PsiTreeUtil.getParentOfType(this, CycStatementWrapper.class) != null;
	}
	
	public Optional<CycExpression> initializer(){
		if(getStub() != null)
			return Optional.empty();
		return PsiUtils.childOfType(this, CycExpression.class);
	}
	
	public boolean hasInferredType(){
		if(getStub() != null)
			return false;
		return PsiUtils.childOfType(this, CycTypeRef.class)
				.map(x -> x.getText().equals("var") || x.getText().equals("val"))
				.orElse(false);
	}
	
	public @NotNull SearchScope getUseScope(){
		return (isLocal() || hasModifier("private")) ? new LocalSearchScope(getContainingFile()) : super.getUseScope();
	}
	
	public @Nullable Icon getIcon(int flags){
		if(isLocal())
			return PlatformIcons.VARIABLE_ICON;
		else
			return PlatformIcons.FIELD_ICON;
	}
	
	public String getName(){
		var stub = getStub();
		if(stub != null)
			return stub.name();
		return super.getName();
	}
}