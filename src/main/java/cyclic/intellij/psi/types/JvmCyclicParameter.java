package cyclic.intellij.psi.types;

import com.intellij.lang.jvm.JvmAnnotation;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.ast.common.CycParameter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

public class JvmCyclicParameter implements JvmParameter{
	
	private static final Map<CycParameter, JvmCyclicParameter> CACHE = new WeakHashMap<>();
	
	private final CycParameter underlying;
	
	private JvmCyclicParameter(CycParameter underlying){
		this.underlying = underlying;
	}
	
	public static JvmCyclicParameter of(CycParameter type){
		if(type == null)
			return null;
		return CACHE.computeIfAbsent(type, JvmCyclicParameter::new);
	}
	
	
	public @NotNull JvmType getType(){
		return underlying.varType();
	}
	
	public boolean hasModifier(@NotNull JvmModifier modifier){
		// TODO: final parameters
		return false;
	}
	
	public JvmAnnotation @NotNull [] getAnnotations(){
		return new JvmAnnotation[0];
	}
	
	public @NonNls @Nullable String getName(){
		return underlying.getName();
	}
	
	public @Nullable PsiElement getSourceElement(){
		return underlying;
	}
}