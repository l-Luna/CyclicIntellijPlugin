package cyclic.intellij.presentation.annotators;

import com.intellij.ide.highlighter.JavaHighlightingColors;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.jvm.JvmClassKind;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.ast.CycAnnotation;
import cyclic.intellij.psi.utils.CycTypeReference;
import org.jetbrains.annotations.NotNull;

public class AnnotationAnnotator implements Annotator, DumbAware{
	
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder){
		if(element instanceof CycAnnotation){
			for(PsiElement child : element.getChildren()){
				if(!(child instanceof PsiComment))
					holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
							.textAttributes(JavaHighlightingColors.ANNOTATION_NAME_ATTRIBUTES)
							.range(child.getTextRange())
							.create();
			}
		}
		
		if(element instanceof CycElement
				&& !(element instanceof CycAnnotation)
				&& !DumbService.isDumb(element.getProject())){
			var reference = element.getReference();
			if(reference instanceof CycTypeReference){
				var target = ((CycTypeReference)reference).resolveClass();
				if(target != null && target.getClassKind() == JvmClassKind.ANNOTATION){
					holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
							.textAttributes(JavaHighlightingColors.ANNOTATION_NAME_ATTRIBUTES)
							.range(reference.getAbsoluteRange())
							.create();
				}
			}
		}
	}
}
