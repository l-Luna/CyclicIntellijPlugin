package cyclic.intellij.psi;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.Nullable;

public interface CycQualifiedReference extends CycReference{
	
	boolean isQualified();
	
	void shortenReference();
	
	@Nullable("null if not qualified")
	TextRange getQualifierRange();
}