package cyclic.intellij.psi.utils;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
	
	public @NotNull TextRange getRangeInElement(){
		return id.getTextRangeInParent();
	}
	
	public @Nullable CPsiClass resolveClass(){
		if(id == null)
			return null;
		var p = id.getProject();
		// possible names come from imports
		if(from.getContainingFile() instanceof CycFile){
			CycFile file = (CycFile)from.getContainingFile();
			Optional<String> pkg = file.getPackage().map(CycPackageStatement::getPackageName);
			List<String> candidates = file.getImports().stream()
					.filter(x -> !x.isStatic())
					.map(x -> x.isWildcard() ? (x.getImportName() + "." + id.getText()) : (x.getImportName().endsWith(id.getText()) ? x.getImportName() : null))
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			candidates.add(0, id.getText());
			// TODO: all implicit imports
			candidates.add(1, "java.lang." + id.getText());
			pkg.ifPresent(s -> candidates.add(2, s + "." + id.getText()));
			for(String candidate : candidates){
				var type = ProjectTypeFinder.findByName(p, candidate);
				if(type.isPresent())
					return type.get();
			}
			return null;
		}
		return ProjectTypeFinder.findByName(p, id.getText()).orElse(null);
	}
	
	public @Nullable PsiElement resolve(){
		var cClass = resolveClass();
		return cClass != null ? cClass.declaration() : null;
	}
	
	public @NotNull String getCanonicalText(){
		var cClass = resolveClass();
		return cClass != null ? cClass.fullyQualifiedName() : id.getText();
	}
	
	public PsiElement handleElementRename(@NotNull String name) throws IncorrectOperationException{
		from.setName(name);
		return from;
	}
	
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException{
		if(element instanceof CycType){
			from.setName(((CycType)element).fullyQualifiedName());
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
			String fqName = typeDef.fullyQualifiedName();
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
			return typeDef.fullyQualifiedName().equals(ourId);
	}
}