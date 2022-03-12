package cyclic.intellij.psi.ast.types;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.stubs.StubCycClassList;
import cyclic.intellij.psi.stubs.StubTypes;
import org.jetbrains.annotations.NotNull;

public class CycExtendsClause extends CycClassList<CycExtendsClause>{
	
	public CycExtendsClause(@NotNull ASTNode node){
		super(node);
	}
	
	public CycExtendsClause(@NotNull StubCycClassList<CycExtendsClause> list){
		super(list, StubTypes.CYC_EXTENDS_LIST);
	}
}