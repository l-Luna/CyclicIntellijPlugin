package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CycStatementWrapper extends CycAstElement{
	
	public CycStatementWrapper(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycStatement> inner(){
		return PsiUtils.childOfType(this, CycStatement.class);
	}
}