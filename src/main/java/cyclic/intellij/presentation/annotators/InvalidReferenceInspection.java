package cyclic.intellij.presentation.annotators;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmField;
import com.intellij.lang.jvm.JvmMember;
import com.intellij.lang.jvm.types.JvmArrayType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.ast.CycCall;
import cyclic.intellij.psi.ast.expressions.CycIdExpr;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.types.JvmCyclicField;
import cyclic.intellij.psi.utils.CycTypeReference;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.Visibility;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class InvalidReferenceInspection implements Annotator{
	
	public static final Set<String> PRIMITIVE_TYPE_NAMES = Set.of(
			"boolean", "byte", "short", "char", "int", "long", "float", "double", "var", "val", "void"
	);
	
	public static final String LENGTH_FIELD = "length";
	
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder){
		var ref = element.getReference();
		if(ref instanceof CycTypeReference){
			var target = ((CycTypeReference)ref).resolveClass();
			if(target == null){
				if(!PRIMITIVE_TYPE_NAMES.contains(element.getText())){
					var builder = holder
							.newAnnotation(HighlightSeverity.ERROR, ProblemsHolder.unresolvedReferenceMessage(ref));
					var fixes = ((CycTypeReference)ref).getQuickFixes();
					if(fixes != null)
						for(LocalQuickFix fix : fixes)
							if(fix instanceof IntentionAction) // should always be true
								builder = builder.withFix((IntentionAction)fix);
					builder.create();
				}
			}else{
				var container = PsiTreeUtil.getParentOfType(element, CycType.class);
				if(!Visibility.visibleFrom(target, JvmCyclicClass.of(container))){
					holder.newAnnotation(HighlightSeverity.ERROR, "Type '" + target.getName() + "' is not visible here")
							.range(ref.getAbsoluteRange())
							.create();
				}
			}
		}
		if(ref instanceof CycIdExpr){
			CycIdExpr expr = (CycIdExpr)ref;
			var target = expr.resolveTarget();
			if(target instanceof JvmClass || target instanceof JvmField || target instanceof CycVariable){
				var container = PsiTreeUtil.getParentOfType(element, CycType.class);
				JvmMember elem = target instanceof CycVariable ? JvmCyclicField.of((CycVariable)target) : (JvmMember)target;
				if(!Visibility.visibleFrom(elem, JvmCyclicClass.of(container)))
					holder.newAnnotation(HighlightSeverity.ERROR, "Member '" + elem.getName() + "' is not visible here")
							.range(ref.getAbsoluteRange())
							.create();
			}else{
				var parent = expr.getParent();
				if((target instanceof String || target instanceof PsiPackage) && parent instanceof CycIdExpr)
					return;
				if(expr.id().equals(LENGTH_FIELD)){
					var on = expr.on();
					if(on != null && on.type() instanceof JvmArrayType)
						return;
				}
				holder.newAnnotation(HighlightSeverity.ERROR, "Cannot resolve type, variable, or field '" + expr.id() + "'")
						.range(ref.getAbsoluteRange())
						.create();
			}
		}
		if(ref instanceof CycCall){
			var call = (CycCall)ref;
			var target = call.resolveMethod();
			var name = call.getMethodName().getText();
			if(target == null)
				holder.newAnnotation(HighlightSeverity.ERROR,
								"Cannot resolve method '" + name + "'")
						.range(ref.getAbsoluteRange())
						.create();
			else{
				var container = JvmCyclicClass.of(PsiTreeUtil.getParentOfType(element, CycType.class));
				if(!Visibility.visibleFrom(target, container))
					holder.newAnnotation(HighlightSeverity.ERROR,
									"Method '" + name + "' is not visible here")
							.range(ref.getAbsoluteRange())
							.create();
			}
		}
	}
}