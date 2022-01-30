package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycIdPart extends CycElement{
	
	public CycIdPart(@NotNull ASTNode node){
		super(node);
	}
}