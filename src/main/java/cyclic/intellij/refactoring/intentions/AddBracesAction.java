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
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.ast.common.CycBlock;
import cyclic.intellij.psi.ast.statements.*;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AddBracesAction implements IntentionAction{
	
	public static final Map<Class<? extends CycElement>, String> controlFlowStatements = Map.of(
			CycIfStatement.class, "if",
			CycWhileStatement.class, "while",
			CycDoWhileStatement.class, "do",
			CycForStatement.class, "for",
			CycForeachStatement.class, "for"
	);
	
	@IntentionName
	private String statementType;
	
	public @IntentionName @NotNull String getText(){
		return "Add braces to '" + statementType + "'";
	}
	
	public @NotNull @IntentionFamilyName String getFamilyName(){
		return "Control flow statement without braces";
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
				if(inner != null && !(inner instanceof CycBlock)){
					statementType = controlFlowStatements.get(ctrlStatement.getClass());
					return true;
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
				if(inner != null && !(inner instanceof CycBlock)){
					var p = inner.getParent();
					while(p.getPrevSibling() instanceof PsiWhiteSpace)
						p.getPrevSibling().delete();
					var block = PsiUtils.createStatementWrapperFromText(ctrlStatement, "{\n" + p.getText() + "\n}");
					p.replace(block);
				}
			}
		}
	}
	
	public boolean startInWriteAction(){
		return true;
	}
}
