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
import com.intellij.psi.PsiType;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.psi.CycVariable;
import cyclic.intellij.psi.ast.CycTypeRef;
import cyclic.intellij.psi.ast.common.CycCall;
import cyclic.intellij.psi.ast.expressions.CycCallExpr;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.ast.expressions.CycIdExpr;
import cyclic.intellij.psi.ast.expressions.CycLiteralExpr;
import cyclic.intellij.psi.ast.statements.CycForeachStatement;
import cyclic.intellij.psi.ast.statements.CycStatementWrapper;
import cyclic.intellij.psi.types.CycKind;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class InvalidElementAnnotator implements Annotator, DumbAware{
	
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder){
		if(element instanceof CycExpression || element instanceof CycTypeRef)
			if(element.getTextLength() == 0)
				holder.newAnnotation(HighlightSeverity.ERROR,
								CyclicBundle.message("annotator.missing.element", element instanceof CycTypeRef ? 0 : 1))
						.range(element.getTextRange())
						.create();
		
		if(element instanceof CycLiteralExpr lit && PsiType.CHAR.equals(lit.type())){
			String text = element.getText();
			// parser allows char literals of any length, validate them here
			// chars can only be 3 letters, and not ''', or '\''
			if((text.length() != 3 && !text.equals("'\\''")) || text.equals("'''"))
				holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.invalid.literal.char")).create();
		}
		
		if(element instanceof CycIdExpr id && !DumbService.isDumb(element.getProject())){
			var p = element.getParent();
			var ref = element.getReference();
			boolean isStaticRef = false;
			if(p instanceof CycIdExpr pId){
				var pTarget = pId.resolveTarget();
				if(pTarget instanceof JvmField fTarget && fTarget.hasModifier(JvmModifier.STATIC))
					isStaticRef = true;
				else if(pTarget instanceof CycVariable vTarget && vTarget.hasModifier("static"))
					isStaticRef = true;
			}
			if(p instanceof CycCallExpr cParent){
				var pTarget = cParent.call().map(CycCall::resolveMethod);
				isStaticRef = pTarget.isEmpty() || pTarget.get().hasModifier(JvmModifier.STATIC);
			}
			if(p instanceof CycStatementWrapper){
				var pTarget = PsiUtils.childOfType(p, CycCall.class).map(CycCall::resolveMethod);
				isStaticRef = pTarget.isEmpty() || pTarget.get().hasModifier(JvmModifier.STATIC);
			}
			if(ref != null && !isStaticRef){
				var target = id.resolveTarget();
				if(target instanceof JvmClass jclass){
					if(!(jclass instanceof JvmCyclicClass jcClass && jcClass.getUnderlying().kind() == CycKind.SINGLE))
						if(!(jclass.getClassKind() == JvmClassKind.ENUM && element.getParent() instanceof CycForeachStatement))
							holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.invalid.element.exprNotType"))
									.range(ref.getAbsoluteRange())
									.create();
				}
			}
		}
	}
}