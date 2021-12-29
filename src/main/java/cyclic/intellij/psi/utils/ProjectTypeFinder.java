package cyclic.intellij.psi.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiManager;
import cyclic.intellij.CyclicFileType;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.CycMember;
import cyclic.intellij.psi.CycTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class ProjectTypeFinder{
	
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
	public static Optional<CycTypeDef> find(@NotNull Project in, @NotNull Predicate<CycTypeDef> checker){
		AtomicReference<Optional<CycTypeDef>> ret = new AtomicReference<>(Optional.empty());
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
	
	public static Optional<CycTypeDef> checkTypeAndMembers(@NotNull CycTypeDef type, @NotNull Predicate<CycTypeDef> checker){
		if(checker.test(type))
			return Optional.of(type);
		for(CycMember member : type.getMembers()){
			Optional<CycTypeDef> def = PsiUtils.childOfType(member, CycTypeDef.class);
			if(def.isPresent()){
				var r = checkTypeAndMembers(def.get(), checker);
				if(r.isPresent())
					return r;
			}
		}
		return Optional.empty();
	}
}