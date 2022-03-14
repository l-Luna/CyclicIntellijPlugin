package cyclic.intellij.asJvm;

import com.intellij.lang.jvm.JvmElement;
import com.intellij.lang.jvm.source.JvmDeclarationSearcher;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.common.CycParameter;
import cyclic.intellij.psi.ast.common.CycVariableDef;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.types.JvmCyclicField;
import cyclic.intellij.psi.types.JvmCyclicMethod;
import cyclic.intellij.psi.types.JvmCyclicParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class CyclicJvmDeclarationSearcher implements JvmDeclarationSearcher{
	
	public @NotNull Collection<JvmElement> findDeclarations(@NotNull PsiElement element){
		if(element instanceof CycType)
			return Collections.singleton(JvmCyclicClass.of((CycType)element));
		if(element instanceof CycMethod)
			return Collections.singleton(JvmCyclicMethod.of((CycMethod)element));
		if(element instanceof CycVariableDef && !((CycVariableDef)element).isLocalVar())
			return Collections.singleton(JvmCyclicField.of((CycVariableDef)element));
		if(element instanceof CycParameter)
			return Collections.singleton(JvmCyclicParameter.of((CycParameter)element));
		
		return Collections.emptyList();
	}
}