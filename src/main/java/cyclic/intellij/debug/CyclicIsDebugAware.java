package cyclic.intellij.debug;

import com.intellij.debugger.engine.JavaDebugAware;
import com.intellij.psi.PsiFile;
import cyclic.intellij.psi.CycFile;
import org.jetbrains.annotations.NotNull;

public class CyclicIsDebugAware extends JavaDebugAware{
	
	public boolean isBreakpointAware(@NotNull PsiFile psiFile){
		return psiFile instanceof CycFile;
	}
}