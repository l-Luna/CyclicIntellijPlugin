package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CycModifierList extends CycAstElement{
	
	public CycModifierList(@NotNull ASTNode node){
		super(node);
	}
	
	public boolean hasModifier(String modifier){
		return PsiUtils.streamChildrenOfType(this, CycModifier.class).anyMatch(x -> x.textMatches(modifier));
	}
	
	public List<String> getModifiers(){
		return PsiUtils.streamChildrenOfType(this, CycModifier.class).map(PsiElement::getText).collect(Collectors.toList());
	}
}