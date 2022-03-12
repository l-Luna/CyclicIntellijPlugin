package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import org.jetbrains.annotations.NotNull;

public class CycStatement extends CycAstElement{
	
	public CycStatement(@NotNull ASTNode node){
		super(node);
	}
}