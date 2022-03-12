package cyclic.intellij.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.psi.ast.CycIdPart;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CycDefinition extends PsiElement, PsiReference, PsiNameIdentifierOwner{
	
	@Nullable
	default PsiElement getNameIdentifier(){
		List<PsiElement> ids = PsiUtils.matchingChildren(this, CycIdPart.class::isInstance);
		return ids.size() > 0 ? ids.get(0) : null;
	}
	
	default PsiElement setName(@NotNull String name) throws IncorrectOperationException{
		if(getNameIdentifier() != null)
			getNameIdentifier().replace(PsiUtils.createIdPartFromText(this, name));
		return this;
	}
	
	@NotNull
	default String getName(){
		var name = getNameIdentifier();
		return name == null ? "" : name.getText();
	}
	
	/**
	 * The name, independent of import statements or context.
	 * e.g. cyclic.intellij.psi.CycDefinition, or cyclic.intellij.CyclicIcons.CYCLIC_FILE, or ...CycDefinition::setName
	 */
	default String fullyQualifiedName(){
		return getName();
	}
	
	default int getTextOffset(){
		return getNameIdentifier() != null ? getNameIdentifier().getTextOffset() : 0;
	}
	
	default PsiReference getReference(){
		return getNameIdentifier() != null ? this : null;
	}
	
	@NotNull
	default PsiElement getElement(){
		return this;
	}
	
	@NotNull
	default TextRange getRangeInElement(){
		return getNameIdentifier().getTextRangeInParent();
	}
	
	@Nullable
	default PsiElement resolve(){
		return this;
	}
	
	@NotNull
	default String getCanonicalText(){
		return fullyQualifiedName();
	}
	
	default PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException{
		return setName(newElementName);
	}
	
	default PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException{
		throw new IncorrectOperationException();
	}
	
	default boolean isReferenceTo(@NotNull PsiElement element){
		return element.equals(this);
	}
	
	default boolean isSoft(){
		return false;
	}
}
