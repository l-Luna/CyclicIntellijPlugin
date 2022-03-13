package cyclic.intellij.psi.types;

import com.intellij.lang.jvm.*;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.ast.CycMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("UnstableApiUsage")
public class JvmCyclicMethod implements JvmMethod{
	
	private static final Map<CycMethod, JvmCyclicMethod> CACHE = new WeakHashMap<>();
	
	private final CycMethod underlying;
	
	private JvmCyclicMethod(CycMethod underlying){
		this.underlying = underlying;
	}
	
	public static JvmCyclicMethod of(CycMethod method){
		if(method == null)
			return null;
		return CACHE.computeIfAbsent(method, JvmCyclicMethod::new);
	}
	
	public boolean isConstructor(){
		return false;
	}
	
	public @NotNull JvmClass getContainingClass(){
		return JvmCyclicClass.of(underlying.containingType());
	}
	
	public @NotNull String getName(){
		return underlying.getName();
	}
	
	public @Nullable JvmType getReturnType(){
		return underlying.returnType();
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
		if(modifier == JvmModifier.ABSTRACT && getContainingClass().getClassKind() == JvmClassKind.INTERFACE)
			if(underlying.hasSemicolon()) // note that a semicolon does not mean abstract in classes
				return true;
		return underlying.hasModifier(modifier.name().toLowerCase(Locale.ROOT));
	}
	
	public JvmAnnotation @NotNull [] getAnnotations(){
		return new JvmAnnotation[0];
	}
	
	public @Nullable PsiElement getSourceElement(){
		return underlying;
	}
	
	public CycMethod getUnderlying(){
		return underlying;
	}
}