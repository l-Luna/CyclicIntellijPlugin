package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.types.CPsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycCall extends CycElement{
	
	public CycCall(@NotNull ASTNode node){
		super(node);
	}
	
	@Nullable
	public CPsiMethod resolve(@Nullable CycExpression on){
		
		return null;
	}
}