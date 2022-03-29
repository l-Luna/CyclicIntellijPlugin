package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.ast.common.CycBlock;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

// not a CycStatement, part of CycTryCatchStatement
public class CycFinallyBlock extends CycAstElement{
	
	public CycFinallyBlock(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycStatement> body(){
		return PsiUtils.childOfType(this, CycBlock.class).map(x -> x);
	}
}