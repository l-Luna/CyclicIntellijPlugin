package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import org.jetbrains.annotations.NotNull;

public class CycModifier extends CycAstElement{
	
	public CycModifier(@NotNull ASTNode node){
		super(node);
	}
}