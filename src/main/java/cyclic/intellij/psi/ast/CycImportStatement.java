package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.psi.PsiReference;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.*;
import cyclic.intellij.psi.utils.CycTypeReference;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class CycImportStatement extends CycAstElement implements CycIdHolder{
	
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
	
	public boolean importsType(JvmClass cpc){
		return importsType(cpc.getQualifiedName());
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
	
	@Nullable
	public static CycImportStatement followToImport(JvmClass type, Collection<CycImportStatement> list){
		if(type == null)
			return null;
		for(CycImportStatement statement : list)
			if(statement.importsType(type))
				return statement;
		return null;
	}
	
	public static boolean isQualificationRedundant(CycClassReference reference, Collection<CycImportStatement> list){
		return reference.isQualified() && followToImport(reference.resolveClass(), list) != null;
	}
	
	// TODO: consider conflicting short names
	public static boolean isQualificationRedundant(CycClassReference reference){
		CycFile file = reference.containingCyclicFile();
		if(file == null) // so there's no imports
			return false;
		return isQualificationRedundant(reference, file.getImports());
	}
}