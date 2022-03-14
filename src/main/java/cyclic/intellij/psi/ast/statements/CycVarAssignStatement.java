package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import org.jetbrains.annotations.NotNull;

public class CycVarAssignStatement extends CycAstElement implements CycStatement{
	
	public CycVarAssignStatement(@NotNull ASTNode node){
		super(node);
	}
}