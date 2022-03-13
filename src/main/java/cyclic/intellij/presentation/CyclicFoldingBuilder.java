package cyclic.intellij.presentation;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.Tokens;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CyclicFoldingBuilder extends FoldingBuilderEx implements DumbAware{
	
	public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick){
		return buildFoldRegions(root.getNode(), document, quick);
	}
	
	public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document, boolean quick){
		// we don't use `quick` yet, but using FoldingBuilderEx means we can fold immediately
		List<FoldingDescriptor> collected = new ArrayList<>();
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_classDecl) || node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_block)){
			var opBrace = node.findChildByType(Tokens.getFor(CyclicLangLexer.LBRACE));
			var endBrace = node.findChildByType(Tokens.getFor(CyclicLangLexer.RBRACE));
			if(opBrace != null && endBrace != null)
				collected.add(new FoldingDescriptor(node, TextRange.create(opBrace.getStartOffset(), endBrace.getStartOffset() + 1)));
		}
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_imports)){
			var imports = node.getChildren(Tokens.RULE_IMPORT);
			if(imports.length > 1){
				// fold from first ID to last semicolon
				var begin = imports[0].findChildByType(Tokens.getRuleFor(CyclicLangParser.RULE_id));
				var end = imports[imports.length - 1].findChildByType(Tokens.getFor(CyclicLangLexer.SEMICOLON));
				if(begin != null && end != null)
					collected.add(new FoldingDescriptor(node, TextRange.create(begin.getStartOffset(), end.getStartOffset() + 1)));
			}
		}
		Arrays.stream(node.getChildren(null))
				.map(x -> buildFoldRegions(x, document))
				.flatMap(Arrays::stream)
				.forEach(collected::add);
		return collected.toArray(FoldingDescriptor[]::new);
	}
	
	public @Nullable String getPlaceholderText(@NotNull ASTNode node){
		if(node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_imports))
			return "...";
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
		return node.getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_imports);
	}
}
