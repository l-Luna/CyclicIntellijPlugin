package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class CycTypeDefinition extends CycDefinition{
	
	public CycTypeDefinition(@NotNull ASTNode node){
		super(node);
	}
	
	public String getFullyQualifiedName(){
		// TODO: check file for CycPackageDefinition
		return super.getFullyQualifiedName();
	}
	
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException{
		// also change the file name
		getContainingFile().setName(name + ".cyc");
		return super.setName(name);
	}
}