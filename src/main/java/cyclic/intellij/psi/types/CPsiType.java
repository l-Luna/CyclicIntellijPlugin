package cyclic.intellij.psi.types;

import com.intellij.psi.*;
import cyclic.intellij.psi.utils.CycVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// PsiClass/JvmClass are too complex to implement at the moment
public interface CPsiType{
	
	enum Kind{
		CLASS, INTERFACE, ENUM, RECORD, ANNOTATION, SINGLE, CONSTRUCTED
	}
	
	static CPsiType fromPsi(PsiType from){
		if(from instanceof PsiClassType)
			return JPsiType.of(((PsiClassType)from).resolve());
		if(from instanceof PsiArrayType)
			return fromPsi(((PsiArrayType)from).getComponentType());
		if(from instanceof PsiPrimitiveType)
			return PrimPsiType.byName(((PsiPrimitiveType)from).getName());
		return null;
	}
	
	@Nullable
	PsiElement declaration();
	
	@NotNull
	String name();
	
	@NotNull
	String packageName();
	
	@NotNull
	String fullyQualifiedName();
	
	@NotNull
	Kind kind();
	
	boolean isFinal();
	
	@NotNull
	List<? extends CPsiMethod> methods();
	
	@NotNull
	List<? extends CycVariable> fields();
}