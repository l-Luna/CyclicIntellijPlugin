package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

// not a CycStatement, part of CycIfStatement
public class CycElseClause extends CycAstElement{
	
	public CycElseClause(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycStatement> body(){
		return PsiUtils.childOfType(this, CycStatementWrapper.class)
				.flatMap(CycStatementWrapper::inner);
	}
}