package cyclic.intellij.presentation.annotators;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPrimitiveType;
import cyclic.intellij.psi.elements.CycExpression;
import cyclic.intellij.psi.elements.CycVariableAssignment;
import cyclic.intellij.psi.elements.CycVariableDef;
import cyclic.intellij.psi.utils.JvmClassUtils;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import static cyclic.intellij.psi.utils.JvmClassUtils.name;

public class TypeCheckingAnnotator implements Annotator{
	
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder){
		// TODO: use the proper invalid type tooltip
		// it's mostly handled by com.intellij.codeInsight.daemon.impl.analysis.HighlightUtil.createIncompatibleTypesTooltip in IJ
		// but that's package-private
		if(element instanceof CycVariableAssignment){
			var left = PsiUtils.childOfType(element, CycExpression.class, 0);
			var right = PsiUtils.childOfType(element, CycExpression.class, 1);
			if(left.isPresent() && right.isPresent()){
				var lType = left.get().type();
				var rType = right.get().type();
				if(rType != null && lType != null && rType != PsiPrimitiveType.NULL && !JvmClassUtils.isConvertibleTo(rType, lType))
					holder.newAnnotation(HighlightSeverity.ERROR,
									"Incompatible types: '" + name(rType) + "' is not assignable to '" + name(lType) + "'")
							.range(right.get())
							.create();
			}
		}
		if(element instanceof CycVariableDef){
			var rightO = PsiUtils.childOfType(element, CycExpression.class);
			rightO.ifPresent(right -> {
				var rType = right.type();
				var lType = ((CycVariableDef)element).varType();
				if(rType != null && lType != null && rType != PsiPrimitiveType.NULL && !JvmClassUtils.isConvertibleTo(rType, lType))
					holder.newAnnotation(HighlightSeverity.ERROR,
									"Incompatible types: '" + name(rType) + "' is not assignable to '" + name(lType) + "'")
							.range(right)
							.create();
			});
		}
	}
}