package cyclic.intellij.psi.elements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycElement;
import org.jetbrains.annotations.NotNull;

public class CycId extends CycElement{
	
	public CycId(@NotNull ASTNode node){
		super(node);
	}
}