package cyclic.intellij.psi.types;

import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.utils.CycVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PrimPsiType implements CPsiType{
	
	enum Primitive{
		BOOLEAN, BYTE, SHORT, INT, CHAR, LONG, FLOAT, DOUBLE, NULL
	}
	
	public static final PrimPsiType
			BOOLEAN = new PrimPsiType(Primitive.BOOLEAN),
			BYTE = new PrimPsiType(Primitive.BYTE),
			SHORT = new PrimPsiType(Primitive.SHORT),
			INT = new PrimPsiType(Primitive.INT),
			CHAR = new PrimPsiType(Primitive.CHAR),
			LONG = new PrimPsiType(Primitive.LONG),
			FLOAT = new PrimPsiType(Primitive.FLOAT),
			DOUBLE = new PrimPsiType(Primitive.DOUBLE),
			NULL = new PrimPsiType(Primitive.NULL);
	
	public static PrimPsiType byName(String primitiveName){
		switch(primitiveName.toLowerCase(Locale.ROOT)){
			case "boolean": return BOOLEAN;
			case "byte": return BYTE;
			case "short": return SHORT;
			case "int": return INT;
			case "char": return CHAR;
			case "long": return LONG;
			case "float": return FLOAT;
			case "double": return DOUBLE;
			case "null": return NULL;
		}
		return NULL;
	}
	
	private final Primitive type;
	
	private PrimPsiType(Primitive type){
		this.type = type;
	}
	
	public @Nullable PsiElement declaration(){
		return null;
	}
	
	public @NotNull String name(){
		return fullyQualifiedName();
	}
	
	public @NotNull String packageName(){
		return "";
	}
	
	public @NotNull String fullyQualifiedName(){
		return type.name().toLowerCase(Locale.ROOT);
	}
	
	public @NotNull Kind kind(){
		return Kind.CONSTRUCTED;
	}
	
	public boolean isFinal(){
		return true;
	}
	
	public @NotNull List<CPsiMethod> methods(){
		return Collections.emptyList();
	}
	
	public @NotNull List<CycVariable> fields(){
		return Collections.emptyList();
	}
}