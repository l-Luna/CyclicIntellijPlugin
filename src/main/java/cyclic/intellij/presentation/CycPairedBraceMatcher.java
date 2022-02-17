package cyclic.intellij.presentation;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.psi.Tokens;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycPairedBraceMatcher implements PairedBraceMatcher{
	
	public BracePair @NotNull [] getPairs(){
		return new BracePair[] {
				new BracePair(Tokens.getFor(CyclicLangLexer.LBRACE), Tokens.getFor(CyclicLangLexer.RBRACE), true),
				new BracePair(Tokens.getFor(CyclicLangLexer.LPAREN), Tokens.getFor(CyclicLangLexer.RPAREN), false)
				// TODO: pair <> correctly - need to seperate between comparisons and generics
		};
	}
	
	public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lBraceType, @Nullable IElementType contextType){
		return true;
	}
	
	public int getCodeConstructStart(PsiFile file, int openingBraceOffset){
		return openingBraceOffset;
	}
}
