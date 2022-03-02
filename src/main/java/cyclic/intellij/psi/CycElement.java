package cyclic.intellij.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.elements.CycFileWrapper;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.psi.Trees;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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
	
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place){
		boolean cont = true;
		for(PsiElement child : getChildren())
			if(cont && lastParent != child)
				cont = processor.execute(child, state);
		return cont;
	}
	
	public boolean textMatches(String text){
		return getText().equals(text);
	}
	
	public Optional<CycFileWrapper> getContainer(){
		var file = getContainingFile();
		if(file instanceof CycFile)
			return ((CycFile)file).wrapper();
		return Optional.empty();
	}
}