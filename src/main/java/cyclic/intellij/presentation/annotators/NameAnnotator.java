package cyclic.intellij.presentation.annotators;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.jvm.JvmField;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.ast.*;
import cyclic.intellij.psi.ast.expressions.CycIdExpr;
import cyclic.intellij.psi.ast.statements.CycForeachLoop;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import static com.intellij.ide.highlighter.JavaHighlightingColors.*;

public class NameAnnotator implements Annotator, DumbAware{
	
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder){
		if(element instanceof CycMethod){
			var name = ((CycMethod)element).getNameIdentifier();
			if(name != null){
				holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
						.textAttributes(METHOD_DECLARATION_ATTRIBUTES)
						.range(name)
						.create();
			}
		}
		
		if(!DumbService.isDumb(element.getProject())){
			if(element instanceof CycCall){
				var target = ((CycCall)element).resolveMethod();
				if(target != null){
					var name = ((CycCall)element).getMethodName();
					if(target.hasModifier(JvmModifier.STATIC))
						holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
								.textAttributes(STATIC_METHOD_ATTRIBUTES)
								.range(name)
								.create();
				}
			}
			if(element instanceof CycIdExpr
					|| (element instanceof CycVariableDef && !((CycVariableDef)element).isLocalVar())
					|| (element instanceof CycParameter && !((CycParameter)element).isMethodParameter())){
				var target = element instanceof CycIdExpr ? ((CycIdExpr)element).resolveTarget() : element;
				var name = PsiUtils.childOfType(element, CycIdPart.class).orElse(null);
				boolean isStatic = false, isFinal = false, isField = false;
				if(target instanceof JvmField){
					JvmField field = (JvmField)target;
					if(field.hasModifier(JvmModifier.STATIC))
						isStatic = true;
					if(field.hasModifier(JvmModifier.FINAL))
						isFinal = true;
					isField = true;
				}
				if(target instanceof CycVariable){
					CycVariable variable = (CycVariable)target;
					if(variable.hasModifier("static"))
						isStatic = true;
					if(variable.hasModifier("final") || (target instanceof CycParameter && !((CycParameter)target).isMethodParameter()))
						isFinal = true; // record components are a CycParameter
					if(!(target instanceof CycParameter && ((CycParameter)target).isMethodParameter())
							&& !(target instanceof CycVariableDef && ((CycVariableDef)target).isLocalVar())
							&& !(target instanceof CycForeachLoop)) // TODO: better way of doing this lol
						isField = true;
				}
				if(isField && name != null){
					TextAttributesKey highlight =
							(isStatic && isFinal) ? STATIC_FINAL_FIELD_ATTRIBUTES :
									(isStatic) ? STATIC_FIELD_ATTRIBUTES :
											(isFinal) ? INSTANCE_FINAL_FIELD_ATTRIBUTES :
													INSTANCE_FIELD_ATTRIBUTES;
					holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
							.textAttributes(highlight)
							.range(name)
							.create();
				}
			}
		}
	}
}