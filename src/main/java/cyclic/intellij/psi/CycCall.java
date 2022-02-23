package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycCall extends CycElement{
	
	public CycCall(@NotNull ASTNode node){
		super(node);
	}
	
	@Nullable
	public JvmMethod resolve(){
		
		return null;
	}
}