package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycBlock extends CycElement{
	
	public CycBlock(@NotNull ASTNode node){
		super(node);
	}
}