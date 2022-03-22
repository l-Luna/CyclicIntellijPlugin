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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RenameFileToTypeFix extends LocalQuickFixAndIntentionActionOnPsiElement{
	
	private final String newName;
	
	public RenameFileToTypeFix(@Nullable PsiElement element, String newName){
		super(element);
		this.newName = newName;
	}
	
	public void invoke(@NotNull Project project, @NotNull PsiFile file, @Nullable Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement){
		if(startElement instanceof CycType)
			file.setName(newName + ".cyc");
	}
	
	public @IntentionName @NotNull String getText(){
		return CyclicBundle.message("intention.text.renameFileToType", newName);
	}
	
	public @IntentionFamilyName @NotNull String getFamilyName(){
		return CyclicBundle.message("intention.familyName.renameFileToType");
	}
}
