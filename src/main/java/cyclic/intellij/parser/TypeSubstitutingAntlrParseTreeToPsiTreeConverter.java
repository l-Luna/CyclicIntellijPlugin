package cyclic.intellij.parser;

import com.intellij.lang.Language;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import cyclic.intellij.psi.Tokens;
import org.antlr.intellij.adaptor.parser.ANTLRParseTreeToPSIConverter;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class TypeSubstitutingAntlrParseTreeToPsiTreeConverter extends ANTLRParseTreeToPSIConverter{
	
	public TypeSubstitutingAntlrParseTreeToPsiTreeConverter(Language language, Parser parser, PsiBuilder builder){
		super(language, parser, builder);
	}
	
	public void exitEveryRule(ParserRuleContext ctx){
		// do exactly the same thing as our superclass, but use Tokens for element types
		// so we can use our stub element types where needed
		ProgressIndicatorProvider.checkCanceled();
		PsiBuilder.Marker marker = markers.pop();
		marker.done(Tokens.getRuleFor(ctx.getRuleIndex()));
	}
}