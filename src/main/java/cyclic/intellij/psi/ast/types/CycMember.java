package cyclic.intellij.psi.ast.types;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import org.jetbrains.annotations.NotNull;

public class CycMember extends CycAstElement{
	
	public CycMember(@NotNull ASTNode node){
		super(node);
	}
}