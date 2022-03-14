package cyclic.intellij.psi.utils;

import cyclic.intellij.psi.ast.common.CycBlock;
import cyclic.intellij.psi.ast.statements.*;

import java.util.function.Predicate;

// Mirrors the compiler's Flow class with Psi elements.
public class Flow{
	
	public static boolean guaranteedToRun(CycStatement statement, Predicate<CycStatement> condition){
		if(statement == null)
			return false;
		if(condition.test(statement))
			return true;
		if(statement instanceof CycBlock)
			return ((CycBlock)statement).streamBody().anyMatch(k -> guaranteedToRun(k, condition));
		if(statement instanceof CycDoWhileStatement)
			return ((CycDoWhileStatement)statement).body().map(x -> guaranteedToRun(x, condition)).orElse(false);
		if(statement instanceof CycIfStatement)
			return ((CycIfStatement)statement).body().map(x -> guaranteedToRun(x, condition)).orElse(false)
				&& ((CycIfStatement)statement).elseBody().map(x -> guaranteedToRun(x, condition)).orElse(false);
		
		return false;
	}
	
	public static boolean guaranteedToExit(CycStatement statement){
		return guaranteedToRun(statement, k -> k instanceof CycReturnStatement || k instanceof CycThrowStatement);
	}
}