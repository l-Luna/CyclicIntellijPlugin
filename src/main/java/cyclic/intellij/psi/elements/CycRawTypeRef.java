package cyclic.intellij.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.types.ClassTypeImpl;
import cyclic.intellij.psi.utils.CycIdHolder;
import cyclic.intellij.psi.utils.CycTypeReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycRawTypeRef extends CycElement implements CycIdHolder{
	
	public CycRawTypeRef(@NotNull ASTNode node){
		super(node);
	}
	
	public PsiReference getReference(){
		return getIdElement().map(id -> new CycTypeReference(id, this)).orElse(null);
	}
	
	@Nullable
	public JvmType type(){
		switch(getText()){
			case "boolean":
				return PsiType.BOOLEAN;
			case "byte":
				return PsiType.BYTE;
			case "short":
				return PsiType.SHORT;
			case "char":
				return PsiType.CHAR;
			case "int":
				return PsiType.INT;
			case "long":
				return PsiType.LONG;
			case "float":
				return PsiType.FLOAT;
			case "double":
				return PsiType.DOUBLE;
			case "void":
				return PsiType.VOID;
		}
		var ref = getReference();
		if(ref instanceof CycTypeReference)
			return ClassTypeImpl.of(((CycTypeReference)ref).resolveClass());
		return null;
	}
}