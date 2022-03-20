package cyclic.intellij.refactoring;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import cyclic.intellij.parser.LexerAdapter;
import cyclic.intellij.psi.CycDefinition;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.ast.types.CycType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycFindUsagesProvider implements FindUsagesProvider{
	
	public boolean canFindUsagesFor(@NotNull PsiElement psiElement){
		return psiElement instanceof CycDefinition;
	}
	
	public @Nullable @NonNls String getHelpId(@NotNull PsiElement psiElement){
		return null;
	}
	
	public @Nls @NotNull String getType(@NotNull PsiElement element){
		if(element instanceof CycType)
			return "class"; // TODO: type kinds
		return "";
	}
	
	public @Nls @NotNull String getDescriptiveName(@NotNull PsiElement element){
		if(element instanceof CycType)
			return ((CycType)element).fullyQualifiedName();
		return "";
	}
	
	public @Nls @NotNull String getNodeText(@NotNull PsiElement element, boolean useFullName){
		if(element instanceof CycType){
			CycType def = (CycType)element;
			return useFullName ? def.fullyQualifiedName() : def.getName();
		}
		return "";
	}
	
	public @Nullable WordsScanner getWordsScanner(){
		return new DefaultWordsScanner(new LexerAdapter(), Tokens.IDENTIFIERS, Tokens.COMMENTS, Tokens.LITERALS);
	}
}
