package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycModifier extends CycElement{
	
	public CycModifier(@NotNull ASTNode node){
		super(node);
	}
}