package cyclic.intellij.psi;

import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.ast.CycFileWrapper;

import java.util.Optional;

public interface CycElement extends PsiElement{
	
	default Optional<CycFileWrapper> getContainer(){
		var file = getContainingFile();
		if(file instanceof CycFile)
			return ((CycFile)file).wrapper();
		return Optional.empty();
	}
}