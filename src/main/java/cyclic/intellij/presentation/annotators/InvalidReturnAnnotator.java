package cyclic.intellij.presentation.annotators;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.expressions.CycCallExpr;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.ast.statements.CycReturnStatement;
import cyclic.intellij.psi.ast.statements.CycStatementWrapper;
import cyclic.intellij.psi.utils.Flow;
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
			CycMethod method = (CycMethod)element;
			method.body().ifPresent(body -> {
				if(!method.hasModifier("abstract") && !Objects.equals(method.returnType(), PsiPrimitiveType.VOID))
					if(!Flow.guaranteedToExit(body)){
						holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.missing.return"))
								.range(body.getParent().getLastChild())
								.create();
					}
			});
			var mBody = method.getLastChild();
			if(mBody != null){
				if(mBody.getChildren().length > 1){
					// must be an arrow function
					var methodType = method.returnType();
					PsiUtils.childOfType(mBody, CycExpression.class).ifPresentOrElse(value -> {
						var returnType = value.type();
						if(value instanceof CycCallExpr && PsiPrimitiveType.VOID.equals(returnType) && returnType.equals(methodType))
							return; // void v() -> k(); is fine
						checkReturn(holder, methodType, returnType);
					}, () -> {
						var statement = PsiUtils.childOfType(mBody, CycStatementWrapper.class)
								.flatMap(CycStatementWrapper::inner);
						if(statement.isEmpty()){
							if(!Objects.equals(methodType, PsiPrimitiveType.VOID))
								holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.missing.expression")).create();
						}
					});
				}
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
								? CyclicBundle.message("annotator.invalid.return.valueFromVoid")
								: CyclicBundle.message("annotator.invalid.return.type", name(returnType), name(methodType)))
						.create();
			}
		}
	}
}