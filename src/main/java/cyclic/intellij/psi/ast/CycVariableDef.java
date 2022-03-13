package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import cyclic.intellij.psi.CycDefinitionStubElement;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.ast.statements.CycStatement;
import cyclic.intellij.psi.stubs.StubCycField;
import cyclic.intellij.psi.stubs.StubTypes;
import cyclic.intellij.psi.utils.CycModifiersHolder;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;

public class CycVariableDef extends CycDefinitionStubElement<CycVariableDef, StubCycField>
		implements CycVariable, CycModifiersHolder{
	
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
			// don't bother with inferring, not allowed on fields
			var type = PsiUtils.createTypeReferenceFromText(this, stub.typeText());
			return Optional.of((CycTypeRef)type)
					.map(CycTypeRef::asType)
					.orElse(PsiPrimitiveType.NULL);
		}
		
		return PsiUtils.childOfType(this, CycTypeRef.class)
				.map(CycTypeRef::asType)
				// for var/val
				.orElseGet(() -> !isLocalVar() ? PsiPrimitiveType.NULL :
						PsiUtils.childOfType(this, CycExpression.class)
						.map(CycExpression::type)
						.orElse(PsiPrimitiveType.NULL));
	}
	
	public boolean hasModifier(String modifier){
		return CycModifiersHolder.super.hasModifier(modifier);
	}
	
	public boolean isLocalVar(){
		if(getStub() != null)
			return false;
		return PsiTreeUtil.getParentOfType(this, CycStatement.class) != null;
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
		return isLocalVar() ? new LocalSearchScope(getContainingFile()) : super.getUseScope();
	}
	
	public @Nullable Icon getIcon(int flags){
		if(isLocalVar())
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