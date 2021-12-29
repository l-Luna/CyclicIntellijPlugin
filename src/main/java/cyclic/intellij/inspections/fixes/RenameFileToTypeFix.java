package cyclic.intellij.inspections.fixes;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import cyclic.intellij.psi.CycTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RenameFileToTypeFix extends LocalQuickFixAndIntentionActionOnPsiElement{
	
	private final String newName;
	
	public RenameFileToTypeFix(@Nullable PsiElement element, String newName){
		super(element);
		this.newName = newName;
	}
	
	public void invoke(@NotNull Project project, @NotNull PsiFile file, @Nullable Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement){
		if(startElement instanceof CycTypeDef)
			file.setName(newName + ".cyc");
	}
	
	public @IntentionName @NotNull String getText(){
		return "Rename file to '" + newName + ".cyc'";
	}
	
	public @IntentionFamilyName @NotNull String getFamilyName(){
		return "Rename file to match type";
	}
}
