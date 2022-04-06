package cyclic.intellij.presentation.annotators;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmClassKind;
import com.intellij.lang.jvm.JvmField;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiElement;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.psi.CycVariable;
import cyclic.intellij.psi.ast.CycTypeRef;
import cyclic.intellij.psi.ast.common.CycCall;
import cyclic.intellij.psi.ast.expressions.CycCallExpr;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.ast.expressions.CycIdExpr;
import cyclic.intellij.psi.ast.statements.CycForeachStatement;
import org.jetbrains.annotations.NotNull;

public class InvalidElementAnnotator implements Annotator, DumbAware{
	
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder){
		if(element instanceof CycExpression
				|| element instanceof CycTypeRef)
			if(element.getTextLength() == 0)
				holder.newAnnotation(HighlightSeverity.ERROR,
								CyclicBundle.message("annotator.missing.element", element instanceof CycTypeRef ? 0 : 1))
						.range(element.getTextRange())
						.create();
		if(element instanceof CycIdExpr && !DumbService.isDumb(element.getProject())){
			var p = element.getParent();
			var ref = element.getReference();
			boolean isStaticRef = false;
			if(p instanceof CycIdExpr){
				var pTarget = ((CycIdExpr)p).resolveTarget();
				if(pTarget instanceof JvmField && ((JvmField)pTarget).hasModifier(JvmModifier.STATIC))
					isStaticRef = true;
				else if(pTarget instanceof CycVariable && ((CycVariable)pTarget).hasModifier("static"))
					isStaticRef = true;
			}
			if(p instanceof CycCallExpr){
				var pTarget = ((CycCallExpr)p).call().map(CycCall::resolveMethod);
				isStaticRef = pTarget.isEmpty() || pTarget.get().hasModifier(JvmModifier.STATIC);
			}
			if(ref != null && !isStaticRef){
				var target = ((CycIdExpr)element).resolveTarget();
				// TODO: allow singles as values
				if(target instanceof JvmClass){
					if(!(((JvmClass)target).getClassKind() == JvmClassKind.ENUM && element.getParent() instanceof CycForeachStatement))
						holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.invalid.element.exprNotType"))
								.range(ref.getAbsoluteRange())
								.create();
				}
			}
		}
	}
}