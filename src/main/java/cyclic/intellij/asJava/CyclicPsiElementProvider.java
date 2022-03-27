package cyclic.intellij.asJava;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFinder;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ui.EDT;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.ProjectTypeFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CyclicPsiElementProvider extends PsiElementFinder{
	
	public @Nullable PsiClass findClass(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope){
		if(EDT.isCurrentThreadEdt())
			return null;
		if(scope.getProject() == null)
			return null;
		Optional<JvmClass> type = ProjectTypeFinder.find(scope.getProject(), x -> x.fullyQualifiedName().equals(qualifiedName), scope);
		return type
				.map(x -> x instanceof JvmCyclicClass ? ((JvmCyclicClass)x).getUnderlying() : null)
				.map(AsPsiUtil::asPsiClass)
				.orElse(null);
	}
	
	public PsiClass @NotNull [] findClasses(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope){
		var found = findClass(qualifiedName, scope);
		if(found != null)
			return new PsiClass[]{found};
		return new PsiClass[0];
	}
}