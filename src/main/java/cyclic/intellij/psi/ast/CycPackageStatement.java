package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CycPackageStatement extends CycAstElement{
	
	public CycPackageStatement(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable String getPackageName(){
		return getId().map(PsiElement::getText).orElse(null);
	}
	
	public @NotNull Optional<CycId> getId(){
		return PsiUtils.childOfType(this, CycId.class);
	}
	
	public @Nullable PsiPackage resolve(){
		var name = getPackageName();
		return name != null ? JavaPsiFacade.getInstance(getProject()).findPackage(name) : null;
	}
}