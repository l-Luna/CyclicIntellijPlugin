package cyclic.intellij.inspections.fixes;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.psi.CycClassReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShortenReferenceFix extends LocalQuickFixAndIntentionActionOnPsiElement{
	
	String tooltipText;
	
	public ShortenReferenceFix(@Nullable PsiElement element, String tooltipText){
		super(element);
		this.tooltipText = tooltipText;
	}
	
	public void invoke(@NotNull Project project,
	                   @NotNull PsiFile file,
	                   @Nullable Editor editor,
	                   @NotNull PsiElement startElement,
	                   @NotNull PsiElement endElement){
		var ref = startElement.getReference();
		if(ref instanceof CycClassReference)
			((CycClassReference)ref).shortenReference();
	}
	
	public @IntentionName @NotNull String getText(){
		return CyclicBundle.message("intention.text.shortenReference", tooltipText);
	}
	
	public @IntentionFamilyName @NotNull String getFamilyName(){
		return CyclicBundle.message("intention.familyName.shortenReference");
	}
}
