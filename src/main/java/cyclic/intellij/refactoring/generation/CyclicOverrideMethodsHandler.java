package cyclic.intellij.refactoring.generation;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.java.JavaBundle;
import com.intellij.lang.ContextAwareActionHandler;
import com.intellij.lang.LanguageCodeInsightActionHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.JvmClassUtils;
import cyclic.intellij.psi.utils.PsiUtils;
import cyclic.intellij.refactoring.generation.CycOverrideImplementMemberChooser.JvmMethodMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CyclicOverrideMethodsHandler implements ContextAwareActionHandler, LanguageCodeInsightActionHandler{
	
	public boolean isValidFor(Editor editor, PsiFile file){
		if(!(file instanceof CycFile))
			return false;
		return contextClass(editor, file) != null;
	}
	
	public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file){
		if(!(file instanceof CycFile))
			return;
		var container = contextClass(editor, file);
		if(JvmClassUtils.findUnimplementedMethodsFrom(JvmCyclicClass.of(container), /*quick check*/ true, false).isEmpty()){
			HintManager.getInstance().showErrorHint(editor, JavaBundle.message("override.methods.error.no.methods"));
			return;
		}
		
		List<JvmMethodMember> toOverride = CycOverrideImplementMemberChooser.select(container);
		WriteCommandAction.writeCommandAction(project, file).run(() -> generatePrototypes(toOverride, container));
	}
	
	private static void generatePrototypes(List<JvmMethodMember> toOverride, CycType type){
		for(JvmMethodMember member : toOverride){
			var prototype = PsiUtils.generateMethodPrototype(member.method, type);
			var members = type.getMembers();
			var at = members.size() > 0 ? members.get(0) : type.getLastChild().getPrevSibling();
			type.addAfter(prototype, at);
		}
	}
	
	public boolean isAvailableForQuickList(@NotNull Editor editor, @NotNull PsiFile file, @NotNull DataContext dataContext){
		if(!(file instanceof CycFile))
			return false;
		var container = contextClass(editor, file);
		if(container == null)
			return false;
		return !JvmClassUtils.findUnimplementedMethodsFrom(JvmCyclicClass.of(container), /*quick check*/ true, false).isEmpty();
	}
	
	public boolean startInWriteAction(){
		return false;
	}
	
	private static @Nullable CycType contextClass(Editor editor, PsiFile file){
		int offset = editor.getCaretModel().getOffset();
		PsiElement element = file.findElementAt(offset);
		return PsiTreeUtil.getParentOfType(element, CycType.class);
	}
}