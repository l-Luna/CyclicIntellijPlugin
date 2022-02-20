package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.utils.CycModifiersHolder;
import org.jetbrains.annotations.NotNull;

public class CycVariableAssignment extends CycElement{
	
	public CycVariableAssignment(@NotNull ASTNode node){
		super(node);
	}
}