package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycExtendsClause extends CycElement{
	
	public CycExtendsClause(@NotNull ASTNode node){
		super(node);
	}
}