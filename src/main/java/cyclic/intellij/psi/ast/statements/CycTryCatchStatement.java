package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.ast.common.CycBlock;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CycTryCatchStatement extends CycAstElement implements CycStatement{
	
	public CycTryCatchStatement(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycStatement> body(){
		return PsiUtils.childOfType(this, CycBlock.class).map(x -> x); // lol
	}
	
	public Stream<CycCatchBlock> streamCatchBlocks(){
		return PsiUtils.streamChildrenOfType(this, CycCatchBlock.class);
	}
	
	public List<CycCatchBlock> getCatchBlocks(){
		return PsiUtils.childrenOfType(this, CycCatchBlock.class);
	}
	
	public Stream<CycStatement> streamCatchBodies(){
		return streamCatchBlocks()
				.map(CycCatchBlock::body)
				.flatMap(x -> Stream.ofNullable(x.orElse(null)));
	}
	
	public List<CycStatement> getCatchBodies(){
		return streamCatchBodies().collect(Collectors.toList());
	}
	
	public Optional<CycFinallyBlock> finallyBlock(){
		return PsiUtils.childOfType(this, CycFinallyBlock.class);
	}
}