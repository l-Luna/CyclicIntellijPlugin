package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

public class CycDefinitionAstElement extends CycAstElement implements CycDefinition{
	
	public CycDefinitionAstElement(@NotNull ASTNode node){
		super(node);
	}
	
	public String getName(){
		return CycDefinition.super.getName();
	}
	
	public int getTextOffset(){
		return CycDefinition.super.getTextOffset();
	}
	
	public PsiReference getReference(){
		return CycDefinition.super.getReference();
	}
}