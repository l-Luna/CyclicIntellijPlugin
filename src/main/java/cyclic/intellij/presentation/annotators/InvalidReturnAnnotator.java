package cyclic.intellij.presentation.annotators;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.common.CycBlock;
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
			var body = method.getLastChild();
			// TODO: rework to use CycMethod::body
			if(body != null){
				if(body.getChildren().length > 1){
					// must be an arrow function
					var methodType = method.returnType();
					PsiUtils.childOfType(body, CycExpression.class).ifPresentOrElse(value -> {
						var returnType = value.type();
						checkReturn(holder, methodType, returnType);
					}, () -> {
						var statement = PsiUtils.childOfType(body, CycStatementWrapper.class)
								.flatMap(CycStatementWrapper::inner);
						if(statement.isEmpty()){
							if(!Objects.equals(methodType, PsiPrimitiveType.VOID))
								holder.newAnnotation(HighlightSeverity.ERROR, "Expecting expression").create();
						}else if(!method.hasModifier("abstract") && !Objects.equals(method.returnType(), PsiPrimitiveType.VOID))
							if(!Flow.guaranteedToExit(statement.get()))
								holder.newAnnotation(HighlightSeverity.ERROR, "Missing return statement")
										.range(statement.get())
										.create();
					});
				}else{
					// must be a block function
					PsiUtils.childOfType(body, CycBlock.class).ifPresent(block -> {
						if(!method.hasModifier("abstract") && !Objects.equals(method.returnType(), PsiPrimitiveType.VOID))
							if(!Flow.guaranteedToExit(block))
								holder.newAnnotation(HighlightSeverity.ERROR, "Missing return statement")
										.range(block.getLastChild())
										.create();
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
								? "Cannot return a value from a method with a void return type"
								: "Incompatible types: '" + name(returnType) + "' is not assignable to '" + name(methodType) + "'")
						.create();
			}
		}
	}
}