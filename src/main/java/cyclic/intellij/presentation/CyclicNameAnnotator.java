package cyclic.intellij.presentation;

import com.intellij.ide.highlighter.JavaHighlightingColors;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.CycCall;
import cyclic.intellij.psi.CycMethod;
import org.jetbrains.annotations.NotNull;

public class CyclicNameAnnotator implements Annotator{
	
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder){
		if(element instanceof CycMethod){
			var name = ((CycMethod)element).getNameIdentifier();
			if(name != null){
				holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
						.textAttributes(JavaHighlightingColors.METHOD_DECLARATION_ATTRIBUTES)
						.range(name)
						.create();
			}
		}
		// TODO: dumb mode checking?
		if(element instanceof CycCall){
			var target = ((CycCall)element).resolveMethod();
			if(target != null){
				var name = ((CycCall)element).getMethodName();
				if(target.hasModifier(JvmModifier.STATIC))
					holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
							.textAttributes(JavaHighlightingColors.STATIC_METHOD_ATTRIBUTES)
							.range(name)
							.create();
			}
		}
	}
}