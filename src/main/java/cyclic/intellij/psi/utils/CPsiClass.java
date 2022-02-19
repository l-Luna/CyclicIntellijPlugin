package cyclic.intellij.psi.utils;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// PsiClass/JvmClass are too complex to implement at the moment
public interface CPsiClass{
	
	enum Kind{
		CLASS, INTERFACE, ENUM, RECORD, ANNOTATION, SINGLE, CONSTRUCTED
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
}