package cyclic.intellij.psi.utils;

import cyclic.intellij.psi.ast.common.CycBlock;
import cyclic.intellij.psi.ast.statements.*;

import java.util.function.Consumer;
import java.util.function.Predicate;

// Mirrors the compiler's Flow class with Psi elements.
public class Flow{
	
	public static final Predicate<CycStatement> EXITS = k -> k instanceof CycReturnStatement || k instanceof CycThrowStatement;
	
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
		return guaranteedToRun(statement, EXITS);
	}
	
	public static void visitAfterMatching(CycStatement statement, Predicate<CycStatement> condition, Consumer<CycStatement> actor){
		if(statement instanceof CycBlock){
			CycBlock block = (CycBlock)statement;
			var body = block.getBody();
			boolean encountered = false;
			for(CycStatement stat : body){
				if(encountered)
					actor.accept(stat);
				else if(guaranteedToRun(stat, condition))
					encountered = true;
				else
					visitAfterMatching(stat, condition, actor);
			}
		}
		if(statement instanceof CycWhileStatement)
			((CycWhileStatement)statement).body().ifPresent(body -> visitAfterMatching(body, condition, actor));
		if(statement instanceof CycDoWhileStatement)
			((CycDoWhileStatement)statement).body().ifPresent(body -> visitAfterMatching(body, condition, actor));
		if(statement instanceof CycIfStatement){
			CycIfStatement ifStat = (CycIfStatement)statement;
			ifStat.body().ifPresent(body -> visitAfterMatching(body, condition, actor));
			ifStat.elseBody().ifPresent(body -> visitAfterMatching(body, condition, actor));
		}
		if(statement instanceof CycForStatement){
			CycForStatement forStat = (CycForStatement)statement;
			forStat.start().ifPresent(body -> visitAfterMatching(body, condition, actor));
			forStat.updater().ifPresent(body -> visitAfterMatching(body, condition, actor));
			forStat.body().ifPresent(body -> visitAfterMatching(body, condition, actor));
		}
	}
}