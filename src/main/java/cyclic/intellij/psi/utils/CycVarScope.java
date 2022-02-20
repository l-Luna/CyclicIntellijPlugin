package cyclic.intellij.psi.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.CycElement;

import java.util.List;
import java.util.Optional;

public interface CycVarScope extends PsiElement{
	
	List<? extends CycVariable> available();
	
	static Optional<CycVarScope> scopeOf(CycElement e){
		return Optional.ofNullable(PsiTreeUtil.getParentOfType(e, CycVarScope.class));
	}
}