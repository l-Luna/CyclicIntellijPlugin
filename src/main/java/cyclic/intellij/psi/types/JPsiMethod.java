package cyclic.intellij.psi.types;

import com.intellij.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

public class JPsiMethod implements CPsiMethod{
	
	private static final Map<PsiMethod, JPsiMethod> CACHE = new WeakHashMap<>();
	
	private final PsiMethod underlying;
	
	@Contract("null -> null; !null -> !null")
	public static @Nullable JPsiMethod of(@Nullable PsiMethod underlying){
		if(underlying == null)
			return null;
		return CACHE.computeIfAbsent(underlying, JPsiMethod::new);
	}
	
	public JPsiMethod(PsiMethod underlying){
		this.underlying = underlying;
	}
	
	public @NotNull PsiElement declaration(){
		return underlying;
	}
	
	public @Nullable CPsiType returnType(){
		return CPsiType.fromPsi(underlying.getReturnType());
	}
}