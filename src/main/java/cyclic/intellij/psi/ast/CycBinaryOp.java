package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import org.jetbrains.annotations.NotNull;

public class CycBinaryOp extends CycAstElement{
	
	public CycBinaryOp(@NotNull ASTNode node){
		super(node);
	}
}