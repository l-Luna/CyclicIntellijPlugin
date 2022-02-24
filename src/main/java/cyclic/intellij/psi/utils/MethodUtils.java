package cyclic.intellij.psi.utils;

import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.*;
import cyclic.intellij.psi.expressions.CycBinaryExpr;
import cyclic.intellij.psi.expressions.CycParenthesisedExpr;

import java.util.ArrayList;
import java.util.List;

public class MethodUtils{
	
	public static List<CycExpression> getRealArgs(CycCall call){
		var declaredArgs = PsiUtils.childOfType(call, CycArgumentsList.class)
				.map(x -> PsiUtils.childrenOfType(x, CycExpression.class))
				.orElse(List.of());
		if(call.getParent() instanceof CycStatement)
			return declaredArgs;
		
		var args = new ArrayList<>(declaredArgs);
		PsiElement current = call.getParent();
		while(current instanceof CycExpression){
			current = current.getParent();
			if(!(current instanceof CycParenthesisedExpr || current instanceof CycBinaryExpr))
				break;
			if(current instanceof CycBinaryExpr){
				var bin = (CycBinaryExpr)current;
				var symbol = bin.symbol();
				if(!symbol.equals("|>"))
					break;
				bin.left().ifPresent(x -> args.add(0, x));
			}
		}
		return args;
	}
}