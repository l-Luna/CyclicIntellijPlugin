package cyclic.intellij.presentation;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicProjectRunLineMarkerContributor extends RunLineMarkerContributor{
	
	// We need to highlight two cases:
	// - main methods
	// - project files
	// TODO: the main methods case can be handled by JvmRunLineMarkerContributor for us
	
	public @Nullable Info getInfo(@NotNull PsiElement element){
		
		return null;
	}
}