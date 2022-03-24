package cyclic.intellij.psi.types;

import com.intellij.lang.jvm.*;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.ast.CycConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

public class JvmCyclicConstructor implements JvmMethod{
	
	private static final Map<CycConstructor, JvmCyclicConstructor> CACHE = new WeakHashMap<>();
	
	private final CycConstructor underlying;
	
	private JvmCyclicConstructor(CycConstructor underlying){
		this.underlying = underlying;
	}
	
	public static JvmCyclicConstructor of(CycConstructor method){
		if(method == null)
			return null;
		return CACHE.computeIfAbsent(method, JvmCyclicConstructor::new);
	}
	
	public boolean isConstructor(){
		return true;
	}
	
	public @Nullable JvmClass getContainingClass(){
		return JvmCyclicClass.of(underlying.containingType());
	}
	
	public @NotNull String getName(){
		JvmClass c = getContainingClass();
		String q = c != null ? c.getQualifiedName() : "";
		return q != null ? q : "";
	}
	
	public @Nullable JvmType getReturnType(){
		return null;
	}
	
	public JvmParameter @NotNull [] getParameters(){
		return underlying.parameters().stream().map(JvmCyclicParameter::of).toArray(JvmCyclicParameter[]::new);
	}
	
	public boolean isVarArgs(){
		return underlying.isVarargs();
	}
	
	public JvmReferenceType @NotNull [] getThrowsTypes(){
		return new JvmReferenceType[0];
	}
	
	public JvmTypeParameter @NotNull [] getTypeParameters(){
		return new JvmTypeParameter[0];
	}
	
	public boolean hasModifier(@NotNull JvmModifier modifier){
		if(modifier == JvmModifier.PACKAGE_LOCAL)
			return !(underlying.hasModifier("public") || underlying.hasModifier("protected") || underlying.hasModifier("private"));
		return underlying.hasModifier(modifier.name().toLowerCase(Locale.ROOT));
	}
	
	public JvmAnnotation @NotNull [] getAnnotations(){
		return new JvmAnnotation[0];
	}
	
	public @Nullable PsiElement getSourceElement(){
		return underlying;
	}
	
	public CycConstructor getUnderlying(){
		return underlying;
	}
}