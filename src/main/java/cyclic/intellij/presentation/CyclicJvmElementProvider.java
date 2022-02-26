package cyclic.intellij.presentation;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.facade.JvmElementProvider;
import com.intellij.psi.search.GlobalSearchScope;
import cyclic.intellij.psi.utils.ProjectTypeFinder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CyclicJvmElementProvider implements JvmElementProvider{
	
	public @NotNull List<? extends JvmClass> getClasses(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope){
		var project = scope.getProject();
		if(project == null)
			return List.of();
		var aClass = ProjectTypeFinder.find(project, x -> x.fullyQualifiedName().equals(qualifiedName), scope::accept);
		return aClass.map(List::of).orElse(List.of());
	}
}