package cyclic.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface CycVarScope extends PsiElement{
	
	@NotNull
	List<? extends CycVariable> available();
	
	static Optional<CycVarScope> scopeOf(PsiElement e){
		return Optional.ofNullable(PsiTreeUtil.getParentOfType(e, CycVarScope.class));
	}
}