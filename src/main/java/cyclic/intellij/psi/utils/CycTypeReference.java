package cyclic.intellij.psi.utils;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CycTypeReference implements PsiReference{
	
	CycId id;
	CycIdHolder from;
	
	public CycTypeReference(CycId id, CycIdHolder from){
		this.id = id;
		this.from = from;
	}
	
	public @NotNull PsiElement getElement(){
		return from;
	}
	
	public @Nullable CycType resolve(){
		// TODO: check Java types
		if(id == null)
			return null;
		return ProjectTypeFinder.find(id.getProject(), this::matchesType).orElse(null);
	}
	
	public @NotNull TextRange getRangeInElement(){
		return id.getTextRangeInParent();
	}
	
	public @NotNull String getCanonicalText(){
		CycType resolve = resolve();
		return resolve != null ? resolve.getCanonicalText() : "";
	}
	
	public PsiElement handleElementRename(@NotNull String name) throws IncorrectOperationException{
		from.setName(name);
		return from;
	}
	
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException{
		if(element instanceof CycType){
			from.setName(((CycType)element).getFullyQualifiedName());
			return from;
		}
		throw new IncorrectOperationException("Can't bind a CycBaseTypeRef to an element that is not a CycTypeDef");
	}
	
	public boolean isReferenceTo(@NotNull PsiElement element){
		return element instanceof CycType && matchesType((CycType)element);
	}
	
	public boolean isSoft(){
		return false;
	}
	
	public boolean matchesType(CycType typeDef){
		String ourId = id.getText();
		if(ourId == null || ourId.isBlank())
			return false;
		if(from.getContainingFile() instanceof CycFile){
			CycFile file = (CycFile)from.getContainingFile();
			Optional<String> pkg = file.getPackage().map(CycPackageStatement::getPackageName);
			if(!pkg.map(String::isBlank).orElse(true) && typeDef.getPackageName().isBlank()){
				// we're not in the default package -> we can't reference it
				return false;
			}
			String fqName = typeDef.getFullyQualifiedName();
			// check if
			// its FQ-name == our text (plus package name)
			if(fqName.equals(ourId) || (pkg.isPresent() && (pkg.get() + "." + ourId).equals(fqName)))
				return true;
			// it's FQ-name == some import that ends in our text,
			// it's FQ-name == a wildcard import + our text
			//     TODO: static imports for inner types
			//     TODO: also consider target's modifiers
			for(CycImportStatement i : file.getImports())
				if(!i.isStatic()){
					String name = i.getImportName();
					if(i.isWildcard() && fqName.equals(name + "." + ourId))
						return true;
					else if(name.endsWith(ourId) && fqName.equals(name))
						return true;
				}
			return false;
		}else
			return typeDef.getFullyQualifiedName().equals(ourId);
	}
}