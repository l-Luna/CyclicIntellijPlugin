package cyclic.intellij.presentation.annotators;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import cyclic.intellij.parser.CyclicSyntaxHighlighter;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.elements.CycIdPart;
import org.jetbrains.annotations.NotNull;

public class ContextKeywordAnnotator implements Annotator, DumbAware{
	
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder){
		if(Tokens.CONTEXT_KEYWORDS.contains(element.getNode().getElementType())
			&& !(element.getParent() instanceof CycIdPart)){
			holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
					.textAttributes(CyclicSyntaxHighlighter.KEYWORD)
					.create();
		}
	}
}