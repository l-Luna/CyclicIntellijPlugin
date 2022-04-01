package cyclic.intellij.psi.utils;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixProvider;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmClassKind;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.inspections.fixes.AddImportFix;
import cyclic.intellij.psi.CycClassReference;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.CycIdHolder;
import cyclic.intellij.psi.ast.CycFileWrapper;
import cyclic.intellij.psi.ast.CycId;
import cyclic.intellij.psi.ast.CycImportStatement;
import cyclic.intellij.psi.ast.CycPackageStatement;
import cyclic.intellij.psi.ast.types.CycExtendsClause;
import cyclic.intellij.psi.ast.types.CycImplementsClause;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.CycKind;
import cyclic.intellij.psi.types.JvmCyclicClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CycTypeReference implements PsiReference, LocalQuickFixProvider, CycClassReference{
	
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
	
	public @Nullable JvmClass resolveClass(){
		if(id == null)
			return null;
		var p = id.getProject();
		var name = id.getText();
		return ProjectTypeFinder.getByName(p, name, from);
	}
	
	public boolean isQualified(){
		return id.getChildren().length > 1;
	}
	
	public @Nullable CycFile containingCyclicFile(){
		var file = id.getContainingFile();
		return file instanceof CycFile ? (CycFile)file : null;
	}
	
	public void shortenReference(){
		var cClass = resolveClass();
		if(cClass != null){
			var name = cClass.getName();
			if(name != null)
				id.replace(PsiUtils.createIdFromText(id.getParent(), name));
		}
	}
	
	public @Nullable TextRange getQualifierRange(){
		if(!isQualified())
			return null;
		TextRange range = id.getTextRangeInParent();
		var parts = id.getChildren();
		range = range.grown(-parts[parts.length - 1].getTextLength());
		return range;
	}
	
	public @Nullable PsiElement resolve(){
		var cClass = resolveClass();
		return cClass != null ? cClass.getSourceElement() : null;
	}
	
	public @NotNull String getCanonicalText(){
		var cClass = resolveClass();
		return cClass != null ? cClass.getQualifiedName() : id.getText();
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
		if(element instanceof CycType)
			return matchesType((CycType)element);
		return resolve() == element;
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
			//     TODO: also consider target's visibility
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
	
	public LocalQuickFix @Nullable [] getQuickFixes(){
		// we resolve to nothing, see if there exists a type with the right short name
		var shortName = id.getText();
		if(shortName.contains("."))
			return new LocalQuickFix[0];
		
		var project = id.getProject();
		List<JvmClass> candidates = ProjectTypeFinder.findAll(project, x -> x.getName().equals(shortName), null);
		candidates.addAll(Arrays.asList(PsiShortNamesCache.getInstance(project)
				.getClassesByName(shortName, GlobalSearchScope.everythingScope(project))));
		
		var container = JvmCyclicClass.of(PsiTreeUtil.getParentOfType(id, CycType.class));
		if(container != null)
			for(int i = candidates.size() - 1; i >= 0; i--){
				JvmClass candidate = candidates.get(i);
				if(!Visibility.visibleFrom(candidate, container))
					candidates.remove(candidate);
			}
		
		return candidates.stream()
				.map(x -> new AddImportFix(x.getQualifiedName(), id))
				.toArray(LocalQuickFix[]::new);
	}
	
	public Object @NotNull [] getVariants(){
		Predicate<JvmClass> isWrongClause = (aClass) -> {
			CycType in;
			if(PsiTreeUtil.getParentOfType(id, CycImplementsClause.class) != null)
				if(aClass.getClassKind() != JvmClassKind.INTERFACE)
					return true;
			if(PsiTreeUtil.getParentOfType(id, CycExtendsClause.class) != null && (in = PsiTreeUtil.getParentOfType(id, CycType.class)) != null)
				return aClass.hasModifier(JvmModifier.FINAL)
						|| (in.kind() != CycKind.INTERFACE && aClass.getClassKind() == JvmClassKind.INTERFACE)
						|| (in.kind() == CycKind.INTERFACE && aClass.getClassKind() != JvmClassKind.INTERFACE)
						|| aClass.getClassKind() == JvmClassKind.ANNOTATION;
			return false;
		};
		
		return fillCompletion(id, isWrongClause);
	}
	
	public static Object @NotNull [] fillCompletion(PsiElement at, Predicate<JvmClass> isWrongClause){
		List<LookupElementBuilder> list = new ArrayList<>();
		var container = PsiTreeUtil.getParentOfType(at, CycType.class);
		for(JvmClass aClass : ProjectTypeFinder.allVisibleAt(at.getProject(), PsiTreeUtil.getParentOfType(at, CycFileWrapper.class))){
			if(!Visibility.visibleFrom(aClass, JvmCyclicClass.of(container)))
				continue;
			boolean wrongClause = isWrongClause.test(aClass);
			PsiElement decl = aClass.getSourceElement();
			if(decl != null){
				var fqName = aClass.getQualifiedName();
				LookupElementBuilder builder = LookupElementBuilder
						.createWithIcon((PsiNamedElement)decl)
						.withTailText(" " + JvmClassUtils.getPackageName(aClass))
						.withPsiElement(aClass.getSourceElement())
						.withInsertHandler((ctx, elem) -> {
							if(ctx.getFile() instanceof CycFile){
								CycFile cFile = (CycFile)ctx.getFile();
								if(cFile.getImports().stream().noneMatch(imp -> imp.importsType(aClass))
										&& !CycImportStatement.importsType("java.lang.*", fqName)
										&& !CycImportStatement.importsType(cFile.getPackageName() + ".*", fqName))
									AddImportFix.addImport(cFile, fqName);
							}
						});
				if(wrongClause)
					builder = builder.withItemTextForeground(JBColor.RED);
				list.add(builder);
			}
		}
		return list.toArray();
	}
}