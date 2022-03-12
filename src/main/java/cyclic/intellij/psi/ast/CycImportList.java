package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import org.jetbrains.annotations.NotNull;

public class CycImportList extends CycAstElement{
	
	public CycImportList(@NotNull ASTNode node){
		super(node);
	}
}