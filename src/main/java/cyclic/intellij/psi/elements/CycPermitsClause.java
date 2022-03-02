package cyclic.intellij.psi.elements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycElement;
import org.jetbrains.annotations.NotNull;

public class CycPermitsClause extends CycElement{
	
	public CycPermitsClause(@NotNull ASTNode node){
		super(node);
	}
}