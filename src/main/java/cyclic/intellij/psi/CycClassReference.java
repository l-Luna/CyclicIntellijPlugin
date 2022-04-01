package cyclic.intellij.psi;

import com.intellij.lang.jvm.JvmClass;
import org.jetbrains.annotations.Nullable;

public interface CycClassReference extends CycQualifiedReference{
	
	@Nullable
	JvmClass resolveClass();
}