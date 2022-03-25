package cyclic.intellij.psi;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Nullable;

public interface CycClassReference extends PsiReference{
	
	@Nullable
	JvmClass resolveClass();
	
	boolean isQualified();
}