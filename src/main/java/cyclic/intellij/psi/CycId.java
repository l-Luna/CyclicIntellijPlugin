package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycId extends CycElement{
	
	public CycId(@NotNull ASTNode node){
		super(node);
	}
}