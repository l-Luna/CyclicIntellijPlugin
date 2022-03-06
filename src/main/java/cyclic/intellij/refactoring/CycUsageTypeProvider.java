package cyclic.intellij.refactoring;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import cyclic.intellij.psi.elements.*;
import cyclic.intellij.psi.expressions.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.usages.impl.rules.UsageType.*;

public class CycUsageTypeProvider implements UsageTypeProvider{
	
	public @Nullable UsageType getUsageType(@NotNull PsiElement element){
		if(element instanceof CycImportStatement)
			return CLASS_IMPORT;
		if(element instanceof CycClassLiteralExpr)
			return CLASS_CLASS_OBJECT_ACCESS;
		if(element instanceof CycAnnotation)
			return ANNOTATION;
		if(element instanceof CycRawTypeRef){
			var top = PsiTreeUtil.getParentOfType(element, CycTypeRef.class);
			if(top == null)
				return null;
			var parent = top.getParent();
			var expr = parent.getParent();
			if(expr instanceof CycCastExpr)
				return CLASS_CAST_TO;
			if(expr instanceof CycInitialisationExpr)
				return CLASS_NEW_OPERATOR;
			if(expr instanceof CycNewArrayExpr || expr instanceof CycNewListArrayExpr)
				return CLASS_NEW_ARRAY;
			if(parent instanceof CycInstanceOfExpr)
				return CLASS_INSTANCE_OF;
			if(parent instanceof CycExtendsClause || parent instanceof CycImplementsClause)
				return CLASS_EXTENDS_IMPLEMENTS_LIST;
			if(parent instanceof CycPermitsClause)
				return CLASS_PERMITS_LIST;
			if(parent instanceof CycVariableDef)
				return ((CycVariableDef)parent).isLocalVar() ? CLASS_LOCAL_VAR_DECLARATION : CLASS_FIELD_DECLARATION;
			if(parent instanceof CycParameter)
				return ((CycParameter)parent).isMethodParameter() ? CLASS_METHOD_PARAMETER_DECLARATION : CLASS_FIELD_DECLARATION;
			if(parent instanceof CycMethod)
				return CLASS_METHOD_RETURN_TYPE;
		}
		if(element instanceof CycIdExpr){
			var target = ((CycIdExpr)element).resolveTarget();
			if(target instanceof JvmClass)
				return CLASS_STATIC_MEMBER_ACCESS;
		}
		return null;
	}
}
