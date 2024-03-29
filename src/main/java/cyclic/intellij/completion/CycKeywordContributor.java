package cyclic.intellij.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import cyclic.intellij.psi.ast.CycIdPart;
import cyclic.intellij.psi.ast.common.CycBlock;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.ast.expressions.CycLiteralExpr;
import cyclic.intellij.psi.ast.expressions.CycStringLiteralExpr;
import cyclic.intellij.psi.ast.types.CycMemberWrapper;
import cyclic.intellij.psi.ast.types.CycType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.*;

public class CycKeywordContributor extends CompletionContributor{
	
	private static final String[] MODIFIER_KEYWORDS = new String[]{
			"private", "protected", "public", "final", "static", "abstract", "synchronised", "native", "volatile" };
	private static final String[] HEADER_KEYWORDS = new String[]{
			"package", "import", "static", "class", "interface", "enum", "record", "annotation", "single" };
	private static final String[] CLAUSE_KEYWORDS = new String[]{
			"extends", "implements", "permits" };
	private static final String[] EXPRESSION_KEYWORDS = new String[]{
			"null", "true", "false", "this", "super", "instanceof" };
	private static final String[] STATEMENT_KEYWORDS = new String[]{
			"var", "val", "if", "else", "while", "do", "for", "return", "throw", "switch", "assert", "yield", "try", "catch", "finally", "this", "super" };
	
	public CycKeywordContributor(){
		extend(CompletionType.BASIC,
				psiElement().inside(CycType.class),
				new KeywordsCompletionProvider(MODIFIER_KEYWORDS));
		extend(CompletionType.BASIC,
				not(psiElement().inside(CycType.class)),
				new KeywordsCompletionProvider(HEADER_KEYWORDS));
		extend(CompletionType.BASIC,
				and(psiElement().inside(CycType.class), not(psiElement().inside(CycMemberWrapper.class))),
				new KeywordsCompletionProvider(CLAUSE_KEYWORDS));
		extend(CompletionType.BASIC,
				psiElement().inside(CycExpression.class),
				new KeywordsCompletionProvider(EXPRESSION_KEYWORDS));
		extend(CompletionType.BASIC,
				psiElement().inside(CycBlock.class),
				new KeywordsCompletionProvider(STATEMENT_KEYWORDS));
	}
	
	private static class KeywordsCompletionProvider extends CompletionProvider<CompletionParameters>{
		private final String[] KEYWORDS;
		
		private KeywordsCompletionProvider(String[] keywords){
			KEYWORDS = keywords;
		}
		
		@Override
		protected void addCompletions(@NotNull CompletionParameters parameters,
		                              @NotNull ProcessingContext context,
		                              @NotNull CompletionResultSet result){
			PsiElement prev = parameters.getOriginalPosition();
			if(prev instanceof PsiComment
					|| PsiTreeUtil.getParentOfType(prev, CycStringLiteralExpr.class) != null
					|| PsiTreeUtil.getParentOfType(prev, CycLiteralExpr.class) != null
					|| PsiTreeUtil.getParentOfType(prev, CycIdPart.class) != null)
				return;
			for(String keyword : KEYWORDS)
				result.addElement(LookupElementBuilder.create(keyword).bold());
		}
	}
}