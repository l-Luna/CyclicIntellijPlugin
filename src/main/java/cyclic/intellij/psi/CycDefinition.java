package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CycDefinition extends CycElement implements PsiNameIdentifierOwner, PsiReference{
	
	public CycDefinition(@NotNull ASTNode node){
		super(node);
	}
	
	public @Nullable PsiElement getNameIdentifier(){
		List<PsiElement> ids = PsiUtils.matchingChildren(this, x -> Tokens.IDENTIFIERS.contains(x.getNode().getElementType()));
		return ids.size() > 0 ? ids.get(0) : null;
	}
	
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException{
		if(getNameIdentifier() != null)
			getNameIdentifier().replace(PsiUtils.createIdPartFromText(this, name));
		return this;
	}
	
	public String getName(){
		var name = getNameIdentifier();
		return name == null ? "" : name.getText();
	}
	
	/**
	 * The name, independent of import statements or context.
	 * e.g. cyclic.intellij.psi.CycDefinition, or cyclic.intellij.CyclicIcons.CYCLIC_FILE.
	 */
	public String getFullyQualifiedName(){
		return getName();
	}
	
	public int getTextOffset(){
		return getNameIdentifier() != null ? getNameIdentifier().getTextOffset() : 0;
	}
	
	public PsiReference getReference(){
		return getNameIdentifier() != null ? this : null;
	}
	
	public @NotNull PsiElement getElement(){
		return this;
	}
	
	public @NotNull TextRange getRangeInElement(){
		return getNameIdentifier().getTextRangeInParent();
	}
	
	public @Nullable PsiElement resolve(){
		return this;
	}
	
	public @NotNull String getCanonicalText(){
		return getFullyQualifiedName();
	}
	
	public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException{
		return setName(newElementName);
	}
	
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException{
		throw new IncorrectOperationException();
	}
	
	public boolean isReferenceTo(@NotNull PsiElement element){
		return element.equals(this);
	}
	
	public boolean isSoft(){
		return false;
	}
}