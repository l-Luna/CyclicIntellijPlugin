package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.utils.CPsiClass;
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
	
	public boolean importsType(CPsiClass cpc){
		return importsType(cpc.fullyQualifiedName());
	}
	
	public boolean importsType(String fqTypeName){
		if(isStatic())
			return false;
		return importsType(getImportName() + (isWildcard() ? ".*" : ""), fqTypeName);
	}
	
	public static boolean importsType(String importName, String fqTypeName){
		if(importName.endsWith(".*")){
			String baseName = importName.substring(0, importName.length() - 1);
			return fqTypeName.startsWith(baseName) && !fqTypeName.substring(baseName.length()).contains(".");
		}
		return fqTypeName.equals(importName);
	}
}
