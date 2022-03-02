package cyclic.intellij.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.elements.CycExpression;
import cyclic.intellij.psi.elements.CycRawTypeRef;
import cyclic.intellij.psi.elements.CycType;
import cyclic.intellij.psi.expressions.CycIdExpr;
import cyclic.intellij.psi.utils.CycTypeReference;
import org.jetbrains.annotations.NotNull;

public class CycTypeContributor extends CompletionContributor{
	
	public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result){
		PsiElement prev = parameters.getOriginalPosition();
		
		var innerFakeExpr = PsiTreeUtil.getParentOfType(parameters.getPosition(), CycExpression.class);
		// don't put types when a member is expected
		if(innerFakeExpr instanceof CycIdExpr && ((CycIdExpr)innerFakeExpr).on() != null)
			return;
		
		// ID token -> IdPart -> Id -> CycRawTypeRef
		if(!(prev.getParent().getParent().getParent() instanceof CycRawTypeRef) && PsiTreeUtil.getParentOfType(prev, CycType.class) != null){
			var elem = prev.getParent();
			if(elem instanceof CycElement)
				for(Object type : CycTypeReference.fillCompletion((CycElement)elem, x -> false))
					if(type instanceof LookupElement)
						result.addElement((LookupElement)type);
		}
	}
}