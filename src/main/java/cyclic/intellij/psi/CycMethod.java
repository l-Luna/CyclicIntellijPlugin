package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CycMethod extends CycDefinition{
	
	public CycMethod(@NotNull ASTNode node){
		super(node);
	}
	
	public CycType getType(){
		// parent is CycMember, then CycType
		return (CycType)getParent().getParent();
	}
	
	public String fullyQualifiedName(){
		return getType().fullyQualifiedName() + "::" + getName();
	}
}