package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import org.jetbrains.annotations.NotNull;

public class CycVarIncrementStatement extends CycAstElement implements CycStatement{
	
	public CycVarIncrementStatement(@NotNull ASTNode node){
		super(node);
	}
}