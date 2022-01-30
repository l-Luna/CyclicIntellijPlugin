package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycStatement extends CycElement{
	
	public CycStatement(@NotNull ASTNode node){
		super(node);
	}
}