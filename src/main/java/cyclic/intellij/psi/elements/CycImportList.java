package cyclic.intellij.psi.elements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycElement;
import org.jetbrains.annotations.NotNull;

public class CycImportList extends CycElement{
	
	public CycImportList(@NotNull ASTNode node){
		super(node);
	}
}