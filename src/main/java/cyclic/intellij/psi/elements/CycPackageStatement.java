package cyclic.intellij.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CycPackageStatement extends CycElement{
	
	public CycPackageStatement(@NotNull ASTNode node){
		super(node);
	}
	
	@Nullable
	public String getPackageName(){
		return getId().map(PsiElement::getText).orElse(null);
	}
	
	public @NotNull Optional<CycId> getId(){
		return PsiUtils.childOfType(this, CycId.class);
	}
	
	// TODO: resolve against packages
}