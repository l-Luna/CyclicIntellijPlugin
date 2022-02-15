package cyclic.intellij.presentation;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.Tokens;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.eclipse.sisu.space.Streams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

public class CycFoldingBuilder implements FoldingBuilder{
	
	public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document){
		FoldingDescriptor[] descriptors = new FoldingDescriptor[0];
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_classDecl) || node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_block)){
			var opBrace = node.findChildByType(Tokens.getFor(CyclicLangLexer.LBRACE));
			var endBrace = node.findChildByType(Tokens.getFor(CyclicLangLexer.RBRACE));
			if(opBrace != null && endBrace != null){
				descriptors = new FoldingDescriptor[]{
						new FoldingDescriptor(node, TextRange.create(opBrace.getStartOffset(), endBrace.getStartOffset() + 1))};
			}
		}
		// simple recursive approach
		// just add the arrays
		descriptors = Stream.concat(
				Arrays.stream(node.getChildren(null))
					.map(x -> buildFoldRegions(x, document))
					.flatMap(Arrays::stream),
				Arrays.stream(descriptors))
					.toArray(FoldingDescriptor[]::new);
		return descriptors;
	}
	
	public @Nullable String getPlaceholderText(@NotNull ASTNode node){
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_classDecl)
				&& node.findChildByType(Tokens.getRuleFor(CyclicLangParser.RULE_member)) == null){
			return "{}";
		}
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_block)
				&& node.findChildByType(Tokens.getRuleFor(CyclicLangParser.RULE_statement)) == null){
			return "{}";
		}
		return "{...}";
	}
	
	public boolean isCollapsedByDefault(@NotNull ASTNode node){
		return false;
	}
}
