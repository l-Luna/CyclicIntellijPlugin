package cyclic.intellij.psi.utils;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class PrimPsiClass implements CPsiClass{
	
	enum Primitive{
		BOOLEAN, BYTE, SHORT, INT, CHAR, LONG, FLOAT, DOUBLE, NULL
	}
	
	public static final PrimPsiClass
			BOOLEAN = new PrimPsiClass(Primitive.BOOLEAN),
			BYTE = new PrimPsiClass(Primitive.BYTE),
			SHORT = new PrimPsiClass(Primitive.SHORT),
			INT = new PrimPsiClass(Primitive.INT),
			CHAR = new PrimPsiClass(Primitive.CHAR),
			LONG = new PrimPsiClass(Primitive.LONG),
			FLOAT = new PrimPsiClass(Primitive.FLOAT),
			DOUBLE = new PrimPsiClass(Primitive.DOUBLE),
			NULL = new PrimPsiClass(Primitive.NULL);
	
	private final Primitive type;
	
	public PrimPsiClass(Primitive type){
		this.type = type;
	}
	
	public @Nullable PsiElement declaration(){
		return null;
	}
	
	public @NotNull String fullyQualifiedName(){
		return type.name().toLowerCase(Locale.ROOT);
	}
}
