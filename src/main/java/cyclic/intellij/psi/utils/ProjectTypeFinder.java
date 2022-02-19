package cyclic.intellij.psi.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import cyclic.intellij.CyclicFileType;
import cyclic.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProjectTypeFinder{
	
	public static List<CPsiClass> allVisibleAt(Project in, CycElement at){
		// TODO: check visibility modifiers
		var types = findAll(in, x -> true);
		addTypesFromPackage(in, types, "java.lang");
		for(CycImportStatement imp : at.getContainer().map(CycFileWrapper::getImports).orElse(Collections.emptyList())){
			if(imp.isWildcard())
				addTypesFromPackage(in, types, imp.getImportName());
			else if(!imp.isWildcard()){
				var t = JavaPsiFacade.getInstance(in).findClass(imp.getImportName(), GlobalSearchScope.allScope(in));
				if(t != null)
					types.add(JPsiClass.of(t));
			}
		}
		return types;
	}
	
	private static void addTypesFromPackage(Project in, List<CPsiClass> types, String pkg){
		var aPackage = JavaPsiFacade.getInstance(in).findPackage(pkg);
		if(aPackage != null)
			types.addAll(
					Arrays.stream(aPackage.getClasses())
							.map(JPsiClass::of)
							.collect(Collectors.toList()));
	}
	
	public static Optional<CPsiClass> findByName(Project in, String qualifiedName){
		// first check types in project
		// can appear in any file due to inner types
		return find(in, y -> y.fullyQualifiedName().equals(qualifiedName))
				.or(() -> Optional.ofNullable(JPsiClass.of(JavaPsiFacade.getInstance(in).findClass(qualifiedName, GlobalSearchScope.allScope(in)))));
	}
	
	/**
	 * Checks every Cyclic type in a project and returns the first type found that matches the criteria, or an empty
	 * Optional if none exist.
	 *
	 * @param in
	 * 		The project to search in.
	 * @param checker
	 * 		The criteria to search against.
	 * @return The first type found that matches the criteria.
	 */
	public static Optional<CPsiClass> find(@NotNull Project in, @NotNull Predicate<CycType> checker){
		AtomicReference<Optional<CPsiClass>> ret = new AtomicReference<>(Optional.empty());
		ProjectRootManager.getInstance(in).getFileIndex().iterateContent(vf -> {
			if(!vf.isDirectory() && Objects.equals(vf.getExtension(), "cyc") && vf.getFileType() == CyclicFileType.FILE_TYPE){
				CycFile file = (CycFile)PsiManager.getInstance(in).findFile(vf);
				if(file != null){
					var type = file.getTypeDef();
					if(type.isPresent()){
						var checked = checkTypeAndMembers(type.get(), checker);
						if(checked.isPresent()){
							ret.set(checked);
							return false;
						}
					}
				}
			}
			return true;
		});
		return ret.get();
	}
	
	public static List<CPsiClass> findAll(@NotNull Project in, @NotNull Predicate<CycType> checker){
		List<CPsiClass> ret = new ArrayList<>();
		ProjectRootManager.getInstance(in).getFileIndex().iterateContent(vf -> {
			if(!vf.isDirectory() && Objects.equals(vf.getExtension(), "cyc") && vf.getFileType() == CyclicFileType.FILE_TYPE){
				CycFile file = (CycFile)PsiManager.getInstance(in).findFile(vf);
				if(file != null){
					var type = file.getTypeDef();
					if(type.isPresent()){
						var checked = checkTypeAndMembers(type.get(), checker);
						if(checked.isPresent()){
							ret.add(checked.get());
							return true; // don't stop
						}
					}
				}
			}
			return true;
		});
		return ret;
	}
	
	public static Optional<CPsiClass> checkTypeAndMembers(@NotNull CycType type, @NotNull Predicate<CycType> checker){
		if(checker.test(type))
			return Optional.of(type);
		for(CycMember member : type.getMembers()){
			Optional<CycType> def = PsiUtils.childOfType(member, CycType.class);
			if(def.isPresent()){
				var r = checkTypeAndMembers(def.get(), checker);
				if(r.isPresent())
					return r;
			}
		}
		return Optional.empty();
	}
}