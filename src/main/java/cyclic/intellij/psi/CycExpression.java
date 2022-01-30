package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycExpression extends CycElement{
	
	public CycExpression(@NotNull ASTNode node){
		super(node);
	}
}