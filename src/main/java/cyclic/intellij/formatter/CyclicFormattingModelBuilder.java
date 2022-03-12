package cyclic.intellij.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.Tokens;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicFormattingModelBuilder implements FormattingModelBuilder{
	
	public @NotNull FormattingModel createModel(@NotNull FormattingContext ctx){
		CodeStyleSettings style = ctx.getCodeStyleSettings();
		return FormattingModelProvider.createFormattingModelForPsiFile(
				ctx.getContainingFile(),
				new CyclicBlock(
						ctx.getNode(),
						Wrap.createWrap(WrapType.NONE, false),
						null,
						createSpaceBuilder(style)),
				style);
	}
	
	private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings){
		var common = settings.getCommonSettings(CyclicLanguage.LANGUAGE.getID());
		return new SpacingBuilder(settings, CyclicLanguage.LANGUAGE)
				.around(Tokens.RULE_BIN_OP)
					.spaceIf(common.SPACE_AROUND_ASSIGNMENT_OPERATORS)
				.around(Tokens.getRuleFor(CyclicLangParser.RULE_imports))
					.blankLines(1);
	}
	
	public @Nullable TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset){
		return elementAtOffset.getTextRange();
	}
}