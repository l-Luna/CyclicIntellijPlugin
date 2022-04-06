package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import org.jetbrains.annotations.NotNull;

public class CycBreakStatement extends CycAstElement implements CycStatement{
	
	public CycBreakStatement(@NotNull ASTNode node){
		super(node);
	}
}