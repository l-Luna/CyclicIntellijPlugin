package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycCall extends CycElement{
	
	public CycCall(@NotNull ASTNode node){
		super(node);
	}
}