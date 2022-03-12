package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import org.jetbrains.annotations.NotNull;

public class CycVariableAssignment extends CycAstElement{
	
	public CycVariableAssignment(@NotNull ASTNode node){
		super(node);
	}
}