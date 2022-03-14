package cyclic.intellij.psi.ast.common;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.ast.statements.CycStatement;
import org.jetbrains.annotations.NotNull;

public class CycInitialisation extends CycAstElement implements CycStatement{
	
	public CycInitialisation(@NotNull ASTNode node){
		super(node);
	}
}