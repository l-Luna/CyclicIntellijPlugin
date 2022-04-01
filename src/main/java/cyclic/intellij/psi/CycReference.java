package cyclic.intellij.psi;

import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Nullable;

public interface CycReference extends PsiReference{
	
	@Nullable
	CycFile containingCyclicFile();
}