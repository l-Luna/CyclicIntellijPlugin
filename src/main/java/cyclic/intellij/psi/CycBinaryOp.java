package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycBinaryOp extends CycElement{
	
	public CycBinaryOp(@NotNull ASTNode node){
		super(node);
	}
}