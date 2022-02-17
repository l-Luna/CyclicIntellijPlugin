package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycImportList extends CycElement{
	
	public CycImportList(@NotNull ASTNode node){
		super(node);
	}
}