package cyclic.intellij.presentation;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil;
import com.intellij.psi.PsiElement;
import cyclic.intellij.parser.CyclicSyntaxHighlighter;
import cyclic.intellij.psi.CycMethod;
import cyclic.intellij.psi.CycType;
import cyclic.intellij.psi.CycTypeRef;
import cyclic.intellij.psi.types.CycKind;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.JvmClassUtils;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CyclicDocumentationProvider extends AbstractDocumentationProvider{
	
	private static final Set<String> PRIMITIVE_NAMES = Set.of("boolean", "byte", "short", "char", "int", "long", "float", "double");
	private static final Set<String> INFERRED_TYPES = Set.of("var", "val");
	
	public @Nullable @Nls String generateDoc(PsiElement element, @Nullable PsiElement originalElement){
		if(element instanceof CycType){
			var type = (CycType)element;
			var current = new StringBuilder(DocumentationMarkup.DEFINITION_START);
			
			appendModifiers(current, type::hasModifier);
			
			if(type.kind() != CycKind.CONSTRUCTED)
				appendKeyword(current, type.kind().name().toLowerCase(Locale.ROOT) + " ");
			
			appendId(current, type.getName());
			
			current.append(DocumentationMarkup.DEFINITION_END);
			return current.toString();
		}
		
		if(element instanceof CycMethod){
			var method = (CycMethod)element;
			var current = new StringBuilder(DocumentationMarkup.DEFINITION_START);
			
			appendModifiers(current, method::hasModifier);
			
			method.returns().ifPresent(x -> {
				appendType(current, x);
				current.append(" ");
			});
			
			appendId(current, method.getName());
			
			if(method.parameters().size() == 0)
				appendId(current, "()");
			else{
				appendId(current, "(");
				for(var param : method.parameters()){
					current.append("\n    ");
					appendType(current, param.getTypeName().orElse(null));
					current.append(" ");
					appendId(current, param.varName());
				}
				current.append("\n)");
			}
			
			current.append(DocumentationMarkup.DEFINITION_END);
			return current.toString();
		}
		
		if(element instanceof CycVariable){
			var variable = (CycVariable)element;
			var current = new StringBuilder(DocumentationMarkup.DEFINITION_START);
			
			appendModifiers(current, variable::hasModifier);
			appendType(current, PsiUtils.childOfType(element, CycTypeRef.class).orElse(null), variable::varType);
			current.append(" ");
			appendId(current, variable.varName());
			
			current.append(DocumentationMarkup.DEFINITION_END);
			return current.toString();
		}
		
		return super.generateDoc(element, originalElement);
	}
	
	protected static void appendKeyword(StringBuilder builder, String append){
		HtmlSyntaxInfoUtil.appendStyledSpan(builder, CyclicSyntaxHighlighter.KEYWORD, append, 1);
	}
	
	protected static void appendId(StringBuilder builder, String append){
		HtmlSyntaxInfoUtil.appendStyledSpan(builder, CyclicSyntaxHighlighter.ID, append, 1);
	}
	
	protected static void appendType(StringBuilder builder, @Nullable CycTypeRef type){
		appendType(builder, type, () -> null);
	}
	
	protected static void appendType(StringBuilder builder, @Nullable CycTypeRef type, Supplier<JvmType> actualType){
		// TODO: better highlighting for type names
		if(type == null)
			return;
		var text = type.getText();
		if(INFERRED_TYPES.contains(text)){
			var actual = actualType.get();
			if(actual != null)
				text = JvmClassUtils.name(actual);
		}
		if(PRIMITIVE_NAMES.contains(text))
			appendKeyword(builder, text);
		else
			appendId(builder, text);
	}
	
	protected static void appendModifiers(StringBuilder builder, Predicate<String> hasModifier){
		for(JvmModifier value : JvmModifier.values()){
			var modifierName = value.name().toLowerCase(Locale.ROOT);
			if(hasModifier.test(modifierName))
				appendKeyword(builder, modifierName + " ");
		}
	}
}