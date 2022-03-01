package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CycModifierList extends CycElement{
	
	public CycModifierList(@NotNull ASTNode node){
		super(node);
	}
	
	public boolean hasModifier(String modifier){
		return PsiUtils.childrenOfType(this, CycModifier.class).stream().anyMatch(x -> x.textMatches(modifier));
	}
	
	public List<String> getModifiers(){
		return PsiUtils.childrenOfType(this, CycModifier.class).stream().map(PsiElement::getText).collect(Collectors.toList());
	}
}