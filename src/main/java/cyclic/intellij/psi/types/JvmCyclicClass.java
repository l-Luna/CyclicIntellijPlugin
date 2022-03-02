package cyclic.intellij.psi.types;

import com.intellij.lang.jvm.*;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.elements.CycType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("UnstableApiUsage")
public class JvmCyclicClass implements JvmClass{
	
	private static final Map<CycType, JvmCyclicClass> CACHE = new WeakHashMap<>();
	
	private final CycType underlying;
	
	protected JvmCyclicClass(CycType underlying){
		this.underlying = underlying;
	}
	
	public static JvmCyclicClass of(CycType type){
		if(type == null)
			return null;
		return CACHE.computeIfAbsent(type, JvmCyclicClass::new);
	}
	
	public @Nullable JvmClass getContainingClass(){
		return null;
	}
	
	public @Nullable @NonNls String getName(){
		return underlying.getName();
	}
	
	public @Nullable @NonNls String getQualifiedName(){
		return underlying.fullyQualifiedName();
	}
	
	public @NotNull JvmClassKind getClassKind(){
		return underlying.kind().toJvmKind();
	}
	
	public @Nullable JvmReferenceType getSuperClassType(){
		return ClassTypeImpl.of(underlying.getSuperType());
	}
	
	public JvmReferenceType @NotNull [] getInterfaceTypes(){
		return underlying.getInterfaces().stream().map(ClassTypeImpl::of).toArray(JvmReferenceType[]::new);
	}
	
	public JvmMethod @NotNull [] getMethods(){
		return underlying.methods().stream().map(JvmCyclicMethod::of).toArray(JvmMethod[]::new);
	}
	
	public JvmField @NotNull [] getFields(){
		return underlying.fields().stream().map(JvmCyclicField::of).toArray(JvmField[]::new);
	}
	
	public JvmClass @NotNull [] getInnerClasses(){
		return new JvmClass[0];
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
	
	public CycType getUnderlying(){
		return underlying;
	}
	
	public boolean equals(Object o){
		return o instanceof JvmClass && underlying.fullyQualifiedName().equals(((JvmClass)o).getQualifiedName());
	}
	
	public int hashCode(){
		return underlying.fullyQualifiedName().hashCode();
	}
}