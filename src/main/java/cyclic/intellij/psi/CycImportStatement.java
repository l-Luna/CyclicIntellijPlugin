package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.utils.CycTypeReference;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class CycImportStatement extends CycElement implements CycIdHolder{
	
	public CycImportStatement(@NotNull ASTNode node){
		super(node);
	}
	
	public boolean isStatic(){
		return !PsiUtils.matchingChildren(this, k -> k.getNode().getElementType() == Tokens.getFor(CyclicLangParser.STATIC)).isEmpty();
	}
	
	public boolean isWildcard(){
		return !PsiUtils.matchingChildren(this, k -> k.getNode().getElementType() == Tokens.getFor(CyclicLangParser.STAR)).isEmpty();
	}
	
	public String getImportName(){
		return getIdElement().map(CycId::getText).orElse("");
	}
	
	public PsiReference getReference(){
		if(isWildcard())
			return null;
		return getIdElement().map(id -> new CycTypeReference(id, this)).orElse(null);
	}
}