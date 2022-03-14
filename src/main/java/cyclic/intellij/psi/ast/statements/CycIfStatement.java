package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CycIfStatement extends CycAstElement implements CycStatement{
	
	public CycIfStatement(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycStatement> body(){
		return PsiUtils.childOfType(this, CycStatementWrapper.class)
				.flatMap(CycStatementWrapper::inner);
	}
	
	public Optional<CycElseElement> elsePart(){
		return PsiUtils.childOfType(this, CycElseElement.class);
	}
	
	public Optional<CycStatement> elseBody(){
		return elsePart().flatMap(CycElseElement::body);
	}
}