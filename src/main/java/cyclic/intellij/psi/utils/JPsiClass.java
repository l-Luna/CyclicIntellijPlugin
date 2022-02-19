package cyclic.intellij.psi.utils;

import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

@SuppressWarnings("UnstableApiUsage")
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
	
	public @NotNull String fullyQualifiedName(){
		return Objects.requireNonNull(underlying.getQualifiedName());
	}
	
	public @NotNull Kind kind(){
		switch(underlying.getClassKind()){
			case INTERFACE:
				return Kind.INTERFACE;
			case ENUM:
				return Kind.ENUM;
			case ANNOTATION:
				return Kind.ANNOTATION;
		}
		return Kind.CLASS;
	}
	
	public boolean isFinal(){
		return underlying.hasModifier(JvmModifier.FINAL);
	}
	
	public @NotNull String name(){
		return Objects.requireNonNull(underlying.getName());
	}
	
	public @NotNull String packageName(){
		var fq = underlying.getQualifiedName();
		return Objects.requireNonNull(fq).substring(0, fq.length() - name().length() - 1);
	}
}