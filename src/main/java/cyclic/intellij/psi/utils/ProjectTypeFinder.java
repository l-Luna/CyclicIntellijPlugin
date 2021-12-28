package cyclic.intellij.psi.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiManager;
import cyclic.intellij.CyclicFileType;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.CycMember;
import cyclic.intellij.psi.CycTypeDef;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class ProjectTypeFinder{
	
	public static Optional<CycTypeDef> find(Project in, Predicate<CycTypeDef> checker){
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
	
	public static Optional<CycTypeDef> checkTypeAndMembers(CycTypeDef type, Predicate<CycTypeDef> checker){
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