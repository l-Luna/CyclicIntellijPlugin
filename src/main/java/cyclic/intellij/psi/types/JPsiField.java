package cyclic.intellij.psi.types;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import cyclic.intellij.psi.utils.CycVariable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

public class JPsiField implements CycVariable{
	
	private static final Map<PsiField, JPsiField> CACHE = new WeakHashMap<>();
	
	private final PsiField underlying;
	
	@Contract("null -> null; !null -> !null")
	public static @Nullable JPsiField of(@Nullable PsiField underlying){
		if(underlying == null)
			return null;
		return CACHE.computeIfAbsent(underlying, JPsiField::new);
	}
	
	private JPsiField(PsiField underlying){
		this.underlying = underlying;
	}
	
	public String varName(){
		return underlying.getName();
	}
	
	public CPsiType varType(){
		return CPsiType.fromPsi(underlying.getType());
	}
	
	public @Nullable PsiElement declaration(){
		return underlying;
	}
}