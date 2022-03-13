package cyclic.intellij.presentation.annotators;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.ast.statements.CycReturnStatement;
import cyclic.intellij.psi.utils.JvmClassUtils;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static cyclic.intellij.psi.utils.JvmClassUtils.name;

public class InvalidReturnAnnotator implements Annotator{
	
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder){
		if(element instanceof CycReturnStatement){
			CycReturnStatement stat = (CycReturnStatement)element;
			var container = PsiTreeUtil.getParentOfType(stat, CycMethod.class);
			if(container != null){
				var methodType = container.returnType();
				var returnType = stat.returnType();
				checkReturn(holder, methodType, returnType);
			}
		}
		if(element instanceof CycMethod){
			var arrow = element.getLastChild();
			if(arrow != null && arrow.getChildren().length > 1){
				var methodType = ((CycMethod)element).returnType();
				PsiUtils.childOfType(arrow, CycExpression.class).ifPresentOrElse(value -> {
					var returnType = value.type();
					checkReturn(holder, methodType, returnType);
				}, () -> {
					if(!Objects.equals(methodType, PsiPrimitiveType.VOID)){
						holder.newAnnotation(HighlightSeverity.ERROR, "Expecting expression").create();
					}
				});
			}
		}
	}
	
	private void checkReturn(@NotNull AnnotationHolder holder, JvmType methodType, JvmType returnType){
		if(!Objects.equals(methodType, PsiPrimitiveType.NULL)){
			boolean returnsFromVoid = Objects.equals(methodType, PsiPrimitiveType.VOID) && returnType != null;
			if(returnsFromVoid || !JvmClassUtils.isConvertibleTo(returnType, methodType)){
				// TODO: use the proper invalid type tooltip
				holder.newAnnotation(
						HighlightSeverity.ERROR,
						returnsFromVoid
								? "Cannot return a value from a method with a void return type"
								: "Incompatible types: '" + name(returnType) + "' is not assignable to '" + name(methodType) + "'")
						.create();
			}
		}
	}
}