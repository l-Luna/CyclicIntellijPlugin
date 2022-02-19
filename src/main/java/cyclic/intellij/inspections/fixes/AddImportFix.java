package cyclic.intellij.inspections.fixes;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.CycImportList;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AddImportFix extends LocalQuickFixAndIntentionActionOnPsiElement{
	
	private final String fqName;
	
	public AddImportFix(String name, PsiElement on){
		super(on);
		fqName = name;
	}
	
	public @IntentionName @NotNull String getText(){
		return "Add import for '" + fqName + "'";
	}
	
	public @IntentionFamilyName @NotNull String getFamilyName(){
		return "Add missing import";
	}
	
	public void invoke(@NotNull Project project, @NotNull PsiFile file, @Nullable Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement){
		addImport(file, fqName);
	}
	
	public static void addImport(@NotNull PsiFile file, String fqName){
		if(file instanceof CycFile){
			CycFile cycFile = (CycFile)file;
			cycFile.wrapper().flatMap(w -> PsiUtils.childOfType(w, CycImportList.class)).ifPresent(list -> {
				list.add(PsiUtils.createWhitespace(list, "\n"));
				list.add(PsiUtils.createImportFromText(list, "import " + fqName + ";"));
			});
		}
	}
}