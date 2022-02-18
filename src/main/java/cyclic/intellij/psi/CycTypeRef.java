package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycTypeRef extends CycElement{
	
	public CycTypeRef(@NotNull ASTNode node){
		super(node);
	}
}