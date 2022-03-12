package cyclic.intellij.presentation.find;

import com.intellij.codeInsight.generation.actions.PresentableCodeInsightActionHandler;
import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.idea.ActionsBundle;
import com.intellij.java.JavaBundle;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.types.CycType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicGoToSuperHandler implements PresentableCodeInsightActionHandler{
	
	public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file){
		var elem = file.findElementAt(editor.getCaretModel().getOffset());
		
		// TODO: methods that override from multiple supertypes at once
		var container = PsiTreeUtil.getParentOfType(elem, CycMethod.class, CycType.class);
		PsiElement target = null;
		if(container instanceof CycMethod){
			var superMethod = ((CycMethod)container).overriddenMethod();
			if(superMethod == null)
				return;
			target = superMethod.getSourceElement();
		}
		if(container instanceof CycType){
			var superType = ((CycType)container).getSuperType();
			if(superType == null)
				return;
			target = superType.getSourceElement();
		}
		
		target = target != null ? target.getNavigationElement() : null;
		if(target == null)
			return;
		PsiFile containingFile = target.getContainingFile();
		if(containingFile == null)
			return;
		VirtualFile virtualFile = containingFile.getVirtualFile();
		if(virtualFile == null)
			return;
		Navigatable descriptor =
				PsiNavigationSupport.getInstance().createNavigatable(project, virtualFile, target.getTextOffset());
		descriptor.navigate(true);
	}
	
	public void update(@NotNull Editor editor, @NotNull PsiFile file, Presentation presentation){
		update(editor, file, presentation, null);
	}
	
	public void update(@NotNull Editor editor, @NotNull PsiFile file, Presentation presentation, @Nullable String actionPlace){
		var elem = file.findElementAt(editor.getCaretModel().getOffset());
		var container = PsiTreeUtil.getParentOfType(elem, CycMethod.class, CycType.class);
		boolean useShortName = actionPlace != null && (ActionPlaces.MAIN_MENU.equals(actionPlace) || ActionPlaces.isPopupPlace(actionPlace));
		if(container instanceof CycType){
			presentation.setText(JavaBundle.message(useShortName ? "action.GotoSuperClass.MainMenu.text" : "action.GotoSuperClass.text"));
			presentation.setDescription(JavaBundle.message("action.GotoSuperClass.description"));
		}else{
			presentation.setText(ActionsBundle.actionText(useShortName ? "GotoSuperMethod.MainMenu" : "GotoSuperMethod"));
			presentation.setDescription(ActionsBundle.actionDescription("GotoSuperMethod"));
		}
	}
}