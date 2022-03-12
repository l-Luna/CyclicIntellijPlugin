package cyclic.intellij.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.Tokens;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CyclicBlock extends AbstractBlock{
	
	private final SpacingBuilder spacing;
	
	protected CyclicBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, SpacingBuilder spacing){
		super(node, wrap, alignment);
		this.spacing = spacing;
	}
	
	protected List<Block> buildChildren(){
		return Arrays.stream(myNode.getChildren(null))
				.filter(x -> x.getElementType() != TokenType.WHITE_SPACE)
				.map(x -> new CyclicBlock(
						x,
						Wrap.createWrap(WrapType.NONE, false),
						null,
						spacing))
				.collect(Collectors.toList());
	}
	
	public @Nullable Spacing getSpacing(@Nullable Block child1, @NotNull Block child2){
		return spacing.getSpacing(this, child1, child2);
	}
	
	public Indent getIndent(){
		var type = myNode.getElementType();
		if(type == Tokens.getRuleFor(CyclicLangParser.RULE_member)
				|| type == Tokens.getRuleFor(CyclicLangParser.RULE_statement)
				|| Tokens.COMMENTS.contains(type))
			return Indent.getNormalIndent();
		return Indent.getNoneIndent();
	}
	
	public boolean isLeaf(){
		return myNode.getFirstChildNode() == null;
	}
	
	protected @Nullable Indent getChildIndent(){
		return Indent.getNormalIndent();
	}
}