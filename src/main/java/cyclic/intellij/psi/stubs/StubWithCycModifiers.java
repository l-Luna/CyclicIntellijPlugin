package cyclic.intellij.psi.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.Nullable;

public interface StubWithCycModifiers<Psi extends PsiElement> extends StubElement<Psi>{
	
	@Nullable
	default StubCycModifierList modifiers(){
		return findChildStubByType(StubTypes.CYC_MODIFIER_LIST);
	}
}