package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycMember extends CycElement{
	
	public CycMember(@NotNull ASTNode node){
		super(node);
	}
}