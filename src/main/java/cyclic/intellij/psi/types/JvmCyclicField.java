package cyclic.intellij.psi.types;

import com.intellij.lang.jvm.JvmAnnotation;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmField;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.utils.CycVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("UnstableApiUsage")
public class JvmCyclicField implements JvmField{
	
	private static final Map<CycVariable, JvmCyclicField> CACHE = new WeakHashMap<>();
	
	private final CycVariable underlying;
	
	private JvmCyclicField(CycVariable underlying){
		this.underlying = underlying;
	}
	
	public static JvmCyclicField of(CycVariable var){
		if(var == null)
			return null;
		return CACHE.computeIfAbsent(var, JvmCyclicField::new);
	}
	
	public @Nullable JvmClass getContainingClass(){
		return null;
	}
	
	public @NotNull String getName(){
		return underlying.varName();
	}
	
	public @NotNull JvmType getType(){
		return underlying.varType();
	}
	
	public boolean hasModifier(@NotNull JvmModifier modifier){
		return false;
	}
	
	public JvmAnnotation @NotNull [] getAnnotations(){
		return new JvmAnnotation[0];
	}
	
	public @Nullable PsiElement getSourceElement(){
		return underlying.declaration();
	}
}