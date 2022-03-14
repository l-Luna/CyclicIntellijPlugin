package cyclic.intellij.psi.ast.common;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.ast.statements.CycStatement;
import cyclic.intellij.psi.ast.statements.CycStatementWrapper;
import cyclic.intellij.psi.utils.CycVarScope;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CycBlock extends CycAstElement implements CycVarScope, CycStatement{
	
	public CycBlock(@NotNull ASTNode node){
		super(node);
	}
	
	public List<? extends CycVariable> available(){
		// all wrapped CycVariableDefs
		// plus our super-scope's variable
		var defined = PsiUtils.wrappedChildrenOfType(this, CycVariable.class);
		var available = new ArrayList<>(defined);
		CycVarScope.scopeOf(this).ifPresent(scope -> available.addAll(scope.available()));
		return available;
	}
	
	public Stream<CycStatement> streamBody(){
		return PsiUtils.streamChildrenOfType(this, CycStatementWrapper.class)
				.flatMap(x -> Stream.ofNullable(x.inner().orElse(null)));
	}
	
	public List<CycStatement> getBody(){
		return streamBody().collect(Collectors.toList());
	}
}