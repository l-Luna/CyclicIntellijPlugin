package cyclic.intellij.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import cyclic.intellij.psi.CycImportStatement;
import cyclic.intellij.psi.CycTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycUsageTypeProvider implements UsageTypeProvider{
	
	public @Nullable UsageType getUsageType(@NotNull PsiElement element){
		if(element instanceof CycImportStatement)
			return UsageType.CLASS_IMPORT;
		return null;
	}
}
