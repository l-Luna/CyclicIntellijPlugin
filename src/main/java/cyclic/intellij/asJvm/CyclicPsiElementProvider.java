package cyclic.intellij.asJvm;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFinder;
import com.intellij.psi.search.GlobalSearchScope;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.ProjectTypeFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// so ApplicationProvider sucks and insists on having a PsiClass to use, not a JvmType
// and PsiJavaFacade doesn't look at a JvmElementProvider when looking for *one* class
public class CyclicPsiElementProvider extends PsiElementFinder{
	
	public @Nullable PsiClass findClass(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope){
		if(scope.getProject() == null)
			return null;
		Optional<JvmClass> type = ProjectTypeFinder.find(scope.getProject(), x -> x.fullyQualifiedName().equals(qualifiedName), scope::accept);
		return type
				.map(x -> x instanceof JvmCyclicClass ? ((JvmCyclicClass)x).getUnderlying() : null)
				.map(AsPsiUtil::asPsiClass)
				.orElse(null);
	}
	
	public PsiClass @NotNull [] findClasses(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope){
		// JavaPsiFacade already checks our JvmDeclarationSearcher (even if it doesn't handle it right)
		return new PsiClass[0];
	}
}