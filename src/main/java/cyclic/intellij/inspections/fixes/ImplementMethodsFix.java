package cyclic.intellij.inspections.fixes;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.refactoring.generation.CyclicOverrideMethodsHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ImplementMethodsFix extends LocalQuickFixAndIntentionActionOnPsiElement{
	
	public ImplementMethodsFix(@Nullable PsiElement element){
		super(element);
	}
	
	public void invoke(@NotNull Project project,
	                   @NotNull PsiFile file,
	                   @Nullable Editor editor,
	                   @NotNull PsiElement startElement,
	                   @NotNull PsiElement endElement){
		CyclicOverrideMethodsHandler.selectAndOverrideMethods(project, file, (CycType)startElement);
	}
	
	public @IntentionName @NotNull String getText(){
		return CyclicBundle.message("intention.text.implementMethods");
	}
	
	public @IntentionFamilyName @NotNull String getFamilyName(){
		return CyclicBundle.message("intention.text.implementMethods");
	}
	
	public boolean startInWriteAction(){
		return false;
	}
}