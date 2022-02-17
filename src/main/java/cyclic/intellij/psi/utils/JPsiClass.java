package cyclic.intellij.psi.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

public class JPsiClass implements CPsiClass{
	
	private static final Map<PsiClass, JPsiClass> CACHE = new WeakHashMap<>();
	
	private final PsiClass underlying;
	
	public static @Nullable JPsiClass of(@Nullable PsiClass underlying){
		if(underlying == null)
			return null;
		return CACHE.computeIfAbsent(underlying, JPsiClass::new);
	}
	
	public static @Nullable JPsiClass of(String fullyQualifiedName, Project in){
		return of(JavaPsiFacade.getInstance(in).findClass(fullyQualifiedName, GlobalSearchScope.everythingScope(in)));
	}
	
	private JPsiClass(PsiClass underlying){
		this.underlying = underlying;
	}
	
	public PsiElement declaration(){
		return underlying;
	}
	
	public String fullyQualifiedName(){
		return underlying.getQualifiedName();
	}
}