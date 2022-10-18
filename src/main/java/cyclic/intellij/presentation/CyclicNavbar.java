package cyclic.intellij.presentation;

import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiPackage;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.ast.types.CycType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicNavbar extends StructureAwareNavBarModelExtension{
	
	@NotNull
	protected Language getLanguage(){
		return CyclicLanguage.LANGUAGE;
	}
	
	public @Nullable String getPresentableText(Object object){
		if(object instanceof PsiPackage pkg && pkg.getName() == null)
			return "<default package>";
		return object instanceof PsiNamedElement named ? named.getName() : null;
	}
	
	public @Nullable PsiElement adjustElement(@NotNull PsiElement element){
		if(element instanceof CycFile file && file.getTypeDef().isPresent())
			return file.getTypeDef().get();
		return super.adjustElement(element);
	}
	
	public @Nullable PsiElement getParent(@Nullable PsiElement element){
		if(element instanceof CycType type && type.isTopLevelType())
			return super.getParent(type.getContainingFile());
		return super.getParent(element);
	}
	
	protected boolean acceptParentFromModel(@Nullable PsiElement element){
		return !(element instanceof CycFile file) || file.getTypeDef().isPresent();
	}
}