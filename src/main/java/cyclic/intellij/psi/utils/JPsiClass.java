package cyclic.intellij.psi.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public class JPsiClass implements CPsiClass{
	
	private final PsiClass underlying;
	
	public static @Nullable JPsiClass of(@Nullable PsiClass underlying){
		if(underlying == null)
			return null;
		return new JPsiClass(underlying);
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