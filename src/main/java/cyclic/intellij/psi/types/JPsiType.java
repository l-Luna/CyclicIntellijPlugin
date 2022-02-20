package cyclic.intellij.psi.types;

import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import cyclic.intellij.psi.utils.CycVariable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class JPsiType implements CPsiType{
	
	private static final Map<PsiClass, JPsiType> CACHE = new WeakHashMap<>();
	
	private final PsiClass underlying;
	
	@Contract("null -> null; !null -> !null")
	public static @Nullable JPsiType of(@Nullable PsiClass underlying){
		if(underlying == null)
			return null;
		return CACHE.computeIfAbsent(underlying, JPsiType::new);
	}
	
	public static @Nullable JPsiType of(String fullyQualifiedName, Project in){
		return of(JavaPsiFacade.getInstance(in).findClass(fullyQualifiedName, GlobalSearchScope.everythingScope(in)));
	}
	
	private JPsiType(PsiClass underlying){
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
	
	public @NotNull List<CPsiMethod> methods(){
		return Arrays.stream(underlying.getMethods()).map(JPsiMethod::of).collect(Collectors.toList());
	}
	
	public @NotNull List<CycVariable> fields(){
		return Arrays.stream(underlying.getFields()).map(JPsiField::of).collect(Collectors.toList());
	}
	
	public @NotNull String name(){
		return Objects.requireNonNull(underlying.getName());
	}
	
	public @NotNull String packageName(){
		var fq = underlying.getQualifiedName();
		return Objects.requireNonNull(fq).substring(0, fq.length() - name().length() - 1);
	}
}