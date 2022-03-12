package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import org.jetbrains.annotations.NotNull;

public class CycId extends CycAstElement{
	
	public CycId(@NotNull ASTNode node){
		super(node);
	}
}