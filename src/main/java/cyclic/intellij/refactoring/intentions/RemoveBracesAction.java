package cyclic.intellij.refactoring.intentions;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.psi.ast.common.CycBlock;
import cyclic.intellij.psi.ast.statements.CycForStatement;
import cyclic.intellij.psi.ast.statements.CycStatement;
import cyclic.intellij.psi.ast.statements.CycStatementWrapper;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import static cyclic.intellij.refactoring.intentions.AddBracesAction.controlFlowStatements;

public class RemoveBracesAction implements IntentionAction{
	
	@IntentionName
	private String statementType;
	
	public @IntentionName @NotNull String getText(){
		return CyclicBundle.message("intention.text.braces.remove", statementType);
	}
	
	public @NotNull @IntentionFamilyName String getFamilyName(){
		return CyclicBundle.message("intention.familyName.braces.remove");
	}
	
	public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file){
		PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
		if(element != null){
			var ctrlStatement = element.getParent();
			if(ctrlStatement != null && controlFlowStatements.containsKey(ctrlStatement.getClass())){
				CycStatement inner;
				if(ctrlStatement instanceof CycForStatement){
					inner = ((CycForStatement)ctrlStatement).body()
							.flatMap(CycStatementWrapper::inner)
							.orElse(null);
				}else{
					inner = PsiUtils.childOfType(ctrlStatement, CycStatementWrapper.class)
							.flatMap(CycStatementWrapper::inner)
							.orElse(null);
				}
				if(inner instanceof CycBlock){
					if(PsiUtils.streamChildrenOfType(inner, CycStatementWrapper.class).count() == 1){
						statementType = controlFlowStatements.get(ctrlStatement.getClass());
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException{
		PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
		if(element != null){
			var ctrlStatement = element.getParent();
			if(ctrlStatement != null && controlFlowStatements.containsKey(ctrlStatement.getClass())){
				CycStatement inner;
				if(ctrlStatement instanceof CycForStatement){
					inner = ((CycForStatement)ctrlStatement).body()
							.flatMap(CycStatementWrapper::inner)
							.orElse(null);
				}else{
					inner = PsiUtils.childOfType(ctrlStatement, CycStatementWrapper.class)
							.flatMap(CycStatementWrapper::inner)
							.orElse(null);
				}
				if(inner instanceof CycBlock){
					inner.getFirstChild().delete(); // {
					while(inner.getLastChild().getPrevSibling() instanceof PsiWhiteSpace)
						inner.getLastChild().getPrevSibling().delete(); // extra whitespace
					inner.getLastChild().delete(); // }
				}
			}
		}
	}
	
	public boolean startInWriteAction(){
		return true;
	}
}
