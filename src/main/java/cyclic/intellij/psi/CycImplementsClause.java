package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycImplementsClause extends CycElement{
	
	public CycImplementsClause(@NotNull ASTNode node){
		super(node);
	}
}