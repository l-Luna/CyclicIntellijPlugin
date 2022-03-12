package cyclic.intellij.psi.utils;

import com.intellij.extapi.psi.ASTDelegatePsiElement;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.ast.CycId;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Contains common logic for finding and changing the name of elements that have an immediate CycId child.
 */
public interface CycIdHolder extends CycElement{
	
	default Optional<CycId> getIdElement(){
		return PsiUtils.childOfType(this, CycId.class);
	}
	
	@Nullable
	default String getId(){
		return getIdElement().map(ASTDelegatePsiElement::getText).orElse(null);
	}
	
	default String getId(String orElse){
		return getIdElement().map(ASTDelegatePsiElement::getText).orElse(orElse);
	}
	
	default void setName(String name){
		getIdElement().ifPresent(id -> id.replace(PsiUtils.createIdFromText(this, name)));
	}
}