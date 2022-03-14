package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import org.jetbrains.annotations.NotNull;

public class CycConstructorCallStatement extends CycAstElement implements CycStatement{
	
	public CycConstructorCallStatement(@NotNull ASTNode node){
		super(node);
	}
}