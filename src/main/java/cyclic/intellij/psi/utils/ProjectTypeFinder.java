package cyclic.intellij.psi.utils;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.ast.CycFileWrapper;
import cyclic.intellij.psi.ast.CycImportStatement;
import cyclic.intellij.psi.ast.CycPackageStatement;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.indexes.StubIndexes;
import cyclic.intellij.psi.types.JvmCyclicClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProjectTypeFinder{
	
	@Nullable
	public static JvmClass firstType(Project p, List<String> candidates){
		for(String candidate : candidates){
			var type = ProjectTypeFinder.findByName(p, candidate);
			if(type.isPresent())
				return type.get();
		}
		return null;
	}
	
	public static List<JvmClass> allVisibleAt(Project in, CycFileWrapper wrapper){
		if(wrapper == null)
			return List.of();
		var types = findAll(in, x -> true, null);
		addTypesFromPackage(in, types, "java.lang");
		for(CycImportStatement imp : wrapper.getImports()){
			if(!imp.isStatic())
				if(imp.isWildcard())
					addTypesFromPackage(in, types, imp.getImportName());
				else{
					var t = JavaPsiFacade.getInstance(in).findClass(imp.getImportName(), GlobalSearchScope.allScope(in));
					if(t != null)
						types.add(t);
				}
		}
		return types;
	}
	
	private static void addTypesFromPackage(Project in, List<JvmClass> types, String pkg){
		var aPackage = JavaPsiFacade.getInstance(in).findPackage(pkg);
		if(aPackage != null)
			types.addAll(Arrays.asList(aPackage.getClasses()));
	}
	
	public static Optional<JvmClass> findByName(Project in, String qualifiedName){
		return find(in, y -> y.fullyQualifiedName().equals(qualifiedName), null)
				.or(() -> Optional.ofNullable(JavaPsiFacade.getInstance(in).findClass(qualifiedName, GlobalSearchScope.allScope(in))));
	}
	
	public static Optional<JvmClass> find(@NotNull Project in,
	                                      @NotNull Predicate<CycType> checker,
	                                      @Nullable GlobalSearchScope scope){
		AtomicReference<JvmClass> choice = new AtomicReference<>();
		StubIndex.getInstance().processAllKeys(StubIndexes.TYPES_BY_FQ_NAME, in, name -> {
			var types = StubIndex.getElements(StubIndexes.TYPES_BY_FQ_NAME, name, in, scope, CycType.class);
			for(CycType type : types){
				if(checker.test(type)){
					choice.set(JvmCyclicClass.of(type));
					return false;
				}
			}
			return true;
		});
		return Optional.ofNullable(choice.get());
	}
	
	public static List<JvmClass> findAll(@NotNull Project in,
	                                     @NotNull Predicate<CycType> checker,
	                                     @Nullable GlobalSearchScope scope){
		List<JvmClass> choices = new ArrayList<>();
		StubIndex.getInstance().processAllKeys(StubIndexes.TYPES_BY_FQ_NAME, in, type -> {
			StubIndex.getElements(StubIndexes.TYPES_BY_FQ_NAME, type, in, scope, CycType.class)
					.stream()
					.filter(checker)
					.map(JvmCyclicClass::of)
					.forEach(choices::add);
			return true;
		});
		return choices;
	}
	
	@Nullable
	public static JvmClass getByName(Project p, String name, @Nullable CycElement context){
		return findByName(p, name).orElseGet(() -> {
			var file = context != null ? context.getContainingFile() : null;
			if(file instanceof CycFile)
				return firstType(p, getCandidates((CycFile)file, name));
			return null;
		});
	}
	
	@NotNull
	public static List<String> getCandidates(CycFile file, String id){
		Optional<String> pkg = file.getPackage().map(CycPackageStatement::getPackageName);
		List<String> candidates = file.getImports().stream()
				.filter(x -> !x.isStatic())
				.map(x -> x.isWildcard() ? (x.getImportName() + "." + id) : (x.getImportName().endsWith(id) ? x.getImportName() : null))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		candidates.add(0, id);
		// TODO: all implicit imports
		candidates.add(1, "java.lang." + id);
		pkg.ifPresent(s -> candidates.add(2, s + "." + id));
		return candidates;
	}
}