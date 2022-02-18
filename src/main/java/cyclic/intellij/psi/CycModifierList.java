package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class CycModifierList extends CycElement{
	
	public CycModifierList(@NotNull ASTNode node){
		super(node);
	}
	
	public boolean hasModifier(String modifier){
		return PsiUtils.childrenOfType(this, CycModifier.class).stream().anyMatch(x -> x.textMatches(modifier));
	}
}