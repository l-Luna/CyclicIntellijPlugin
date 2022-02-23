package cyclic.intellij.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.CycRawTypeRef;
import cyclic.intellij.psi.CycType;
import cyclic.intellij.psi.utils.CycTypeReference;
import org.jetbrains.annotations.NotNull;

public class CycTypeCompletionProvider extends CompletionContributor{
	
	public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result){
		PsiElement prev = parameters.getOriginalPosition();
		
		// ID token -> IdPart -> Id -> CycRawTypeRef
		if(!(prev.getParent().getParent().getParent() instanceof CycRawTypeRef) && PsiTreeUtil.getParentOfType(prev, CycType.class) != null){
			var elem = prev.getParent();
			if(elem instanceof CycElement)
				for(Object type : CycTypeReference.fillCompletion((CycElement)elem, x -> true))
					if(type instanceof LookupElement)
						result.addElement((LookupElement)type);
		}
	}
}