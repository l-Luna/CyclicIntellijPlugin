package cyclic.intellij.psi.types;

import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.utils.CycVariable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class ArrayPsiType implements CPsiType{
	
	private static final Map<CPsiType, ArrayPsiType> CACHE = new WeakHashMap<>();
	
	private static CycVariable LENGTH = new CycVariable(){
		public String varName(){
			return "length";
		}
		public CPsiType varType(){
			return PrimPsiType.INT;
		}
	};
	
	private final CPsiType element;
	
	@Contract("null -> null; !null -> !null")
	public static @Nullable ArrayPsiType of(@Nullable CPsiType underlying){
		if(underlying == null)
			return null;
		return CACHE.computeIfAbsent(underlying, ArrayPsiType::new);
	}
	
	private ArrayPsiType(CPsiType element){
		this.element = element;
	}
	
	public CPsiType getElement(){
		return element;
	}
	
	public @Nullable PsiElement declaration(){
		return element.declaration();
	}
	
	public @NotNull String name(){
		return element.name() + "[]";
	}
	
	public @NotNull String packageName(){
		return element.packageName();
	}
	
	public @NotNull String fullyQualifiedName(){
		return element.fullyQualifiedName() + "[]";
	}
	
	public @NotNull Kind kind(){
		return Kind.CONSTRUCTED;
	}
	
	public boolean isFinal(){
		return true;
	}
	
	public @NotNull List<? extends CPsiMethod> methods(){
		return Collections.emptyList();
	}
	
	public @NotNull List<? extends CycVariable> fields(){
		return List.of(LENGTH);
	}
}