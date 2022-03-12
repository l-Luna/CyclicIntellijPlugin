package cyclic.intellij.psi.ast.types;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.stubs.StubCycClassList;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.NotNull;

public class CycPermitsClause extends CycClassList<CycPermitsClause>{
	
	public CycPermitsClause(@NotNull ASTNode node){
		super(node);
	}
	
	public CycPermitsClause(@NotNull StubCycClassList<CycPermitsClause> list){
		super(list, StubTypes.CYC_PERMITS_LIST);
	}
}