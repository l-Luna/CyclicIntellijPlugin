package cyclic.intellij.psi;

import com.intellij.lang.jvm.JvmMethod;
import cyclic.intellij.psi.ast.common.CycParameter;
import cyclic.intellij.psi.ast.statements.CycStatement;
import cyclic.intellij.psi.ast.types.CycType;

import java.util.List;
import java.util.Optional;

public interface CycCodeHolder extends CycVarScope, CycModifiersHolder{
	
	List<CycParameter> parameters();
	
	Optional<CycStatement> body();
	
	JvmMethod toJvm();
	
	CycType containingType();
	
	default List<? extends CycVariable> available(){
		return parameters();
	}
	
	default boolean isVarargs(){
		return parameters().stream().anyMatch(CycParameter::isVarargs);
	}
	
	default boolean isStatic(){
		return hasModifier("static");
	}
}