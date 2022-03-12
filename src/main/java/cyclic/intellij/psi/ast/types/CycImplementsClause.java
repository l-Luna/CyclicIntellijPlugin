package cyclic.intellij.psi.ast.types;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.stubs.StubCycClassList;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.NotNull;

public class CycImplementsClause extends CycClassList<CycImplementsClause>{
	
	public CycImplementsClause(@NotNull ASTNode node){
		super(node);
	}
	
	public CycImplementsClause(@NotNull StubCycClassList<CycImplementsClause> list){
		super(list, StubTypes.CYC_IMPLEMENTS_LIST);
	}
}