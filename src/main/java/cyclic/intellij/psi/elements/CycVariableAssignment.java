package cyclic.intellij.psi.elements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycElement;
import org.jetbrains.annotations.NotNull;

public class CycVariableAssignment extends CycElement{
	
	public CycVariableAssignment(@NotNull ASTNode node){
		super(node);
	}
}