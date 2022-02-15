package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.psi.utils.CPsiClass;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CycType extends CycDefinition implements CPsiClass{
	
	public CycType(@NotNull ASTNode node){
		super(node);
	}
	
	public boolean isTopLevelType(){
		return getParent() instanceof CycFileWrapper;
	}
	
	public String getPackageName(){
		if(getContainingFile() instanceof CycFile){
			CycFile file = (CycFile)getContainingFile();
			return file.getPackage().map(CycPackageStatement::getPackageName).orElse("");
		}
		return "";
	}
	
	public String fullyQualifiedName(){
		PsiFile file = getContainingFile();
		if(file instanceof CycFile)
			return ((CycFile)file).getPackage().map(k -> k.getPackageName() + ".").orElse("") + super.fullyQualifiedName();
		return super.fullyQualifiedName();
	}
	
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException{
		// also change the file name if top level
		if(isTopLevelType())
			getContainingFile().setName(name + ".cyc");
		return super.setName(name);
	}
	
	public List<CycMember> getMembers(){
		return PsiUtils.childrenOfType(this, CycMember.class);
	}
	
	public PsiElement declaration(){
		return this;
	}
}