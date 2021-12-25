package cyclic.intellij.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.psi.Trees;
import org.jetbrains.annotations.NotNull;

public class CycElement extends ASTWrapperPsiElement{
	
	public CycElement(@NotNull ASTNode node){
		super(node);
	}
	
	public PsiElement @NotNull [] getChildren(){
		return Trees.getChildren(this);
	}
	
	public String toString(){
		boolean isRule = getNode().getElementType() instanceof RuleIElementType;
		if(isRule && CyclicLangParser.ruleNames.length < ((RuleIElementType)getNode().getElementType()).getRuleIndex())
			return getClass().getSimpleName() + "(" + CyclicLangParser.ruleNames[((RuleIElementType)getNode().getElementType()).getRuleIndex()] + ")";
		return getClass().getSimpleName();
	}
}