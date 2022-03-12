package cyclic.intellij.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmField;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.lang.jvm.types.JvmArrayType;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.ast.expressions.CycIdExpr;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.CycVarScope;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.JvmClassUtils;
import cyclic.intellij.psi.utils.Visibility;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.intellij.codeInsight.completion.PrioritizedLookupElement.withPriority;
import static com.intellij.codeInsight.lookup.LookupElementBuilder.create;

public class CycExpressionContributor extends CompletionContributor{
	
	public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result){
		var prev = parameters.getOriginalPosition();
		var fakePrev = parameters.getPosition();
		
		var innerFakeExpr = PsiTreeUtil.getParentOfType(fakePrev, CycExpression.class);
		var innerExpr = PsiTreeUtil.getParentOfType(prev, CycExpression.class);
		
		// an expression could be here
		if(innerFakeExpr instanceof CycIdExpr){
			var on = ((CycIdExpr)innerFakeExpr).on();
			// but isn't
			if(on == null){
				// add local variables
				CycVarScope.scopeOf(parameters.getOriginalPosition()).ifPresent(scope -> {
					for(CycVariable variable : scope.available()){
						var decl = variable.declaration();
						var builder =
								create(variable.varName())
										.withPsiElement(decl);
						if(decl != null)
							builder = builder.withIcon(decl.getIcon(0));
						result.addElement(withPriority(builder, 10));
					}
				});
			}else{
				var container = JvmCyclicClass.of(PsiTreeUtil.getParentOfType(innerFakeExpr, CycType.class));
				var type = on.type();
				if(type != null){
					if(type instanceof JvmArrayType)
						result.addElement(withPriority(create("length").withIcon(PlatformIcons.FIELD_ICON), 10));
					else if(type instanceof JvmReferenceType){
						var cType = ((JvmReferenceType)type).resolve();
						if(cType instanceof JvmClass){
							var t = (JvmClass)cType;
							for(JvmField field : t.getFields()){
								if(!Visibility.visibleFrom(field, container))
									continue;
								var decl = field.getSourceElement();
								var builder =
										create(field.getName())
												.withPsiElement(decl)
												.withTypeText(JvmClassUtils.name(field.getType()));
								if(decl != null)
									builder = builder.withIcon(decl.getIcon(0));
								result.addElement(withPriority(builder, 10));
							}
							for(JvmMethod method : t.getMethods()){
								if(method.isConstructor())
									continue;
								if(!Visibility.visibleFrom(method, container))
									continue;
								var decl = method.getSourceElement();
								var builder =
										create(method.getName())
												.withPsiElement(decl)
												.withTailText(
														Arrays.stream(method.getParameters())
																.map(x -> JvmClassUtils.name(x.getType()) + " " + x.getName())
																.collect(Collectors.joining(", ", "(", ")")))
												.withInsertHandler(method.getParameters().length > 0
														? ParenthesesInsertHandler.WITH_PARAMETERS
														: ParenthesesInsertHandler.NO_PARAMETERS);
								if(decl != null)
									builder = builder.withIcon(decl.getIcon(0));
								if(method.getReturnType() != null)
									builder = builder.withTypeText(JvmClassUtils.name(method.getReturnType()));
								result.addElement(withPriority(builder, 10));
							}
						}
					}
				}
				if(on instanceof CycIdExpr){
					var res = ((CycIdExpr)on).resolveTarget();
					if(res instanceof JvmClass)
						result.addElement(create("class").withBoldness(true));
				}
			}
		}
	}
}