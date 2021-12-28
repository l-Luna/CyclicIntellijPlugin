package cyclic.intellij.psi.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.tree.IElementType;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.psi.Tokens;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PsiUtils{
	
	private static PsiElement createFromText(Project project, PsiElement context, String text, IElementType type){
		PsiFileFactoryImpl factory = (PsiFileFactoryImpl)PsiFileFactory.getInstance(project);
		return factory.createElementFromText(text, CyclicLanguage.LANGUAGE, type, context);
	}
	
	public static PsiElement createFromText(@NotNull PsiElement context, String text, IElementType elementType){
		return createFromText(context.getProject(), context, text, elementType);
	}
	
	// yes, unwrapping both times is intentional
	public static PsiElement createIdFromText(@NotNull PsiElement context, String text){
		return createFromText(context, text, Tokens.getFor(CyclicLangLexer.ID)).getFirstChild();
	}
	
	public static PsiElement createIdPartFromText(@NotNull PsiElement context, String text){
		return createIdFromText(context, text).getFirstChild();
	}
	
	@NotNull public static <X> List<X> childrenOfType(@NotNull PsiElement parent, Class<X> filter){
		return Arrays.stream(parent.getChildren())
				.filter(filter::isInstance)
				.map(z -> (X)z)
				.collect(Collectors.toList());
	}
	
	@NotNull public static <X> List<X> wrappedChildrenOfType(@NotNull PsiElement parent, Class<X> filter){
		return Arrays.stream(parent.getChildren())
				.map(PsiElement::getFirstChild)
				.filter(filter::isInstance)
				.map(z -> (X)z)
				.collect(Collectors.toList());
	}
	
	@NotNull public static List<PsiElement> matchingChildren(@NotNull PsiElement parent, Predicate<PsiElement> filter){
		return Arrays.stream(parent.getChildren())
				.filter(filter)
				.collect(Collectors.toList());
	}
	
	@NotNull public static <X extends PsiElement> Optional<X> childOfType(@NotNull PsiElement parent, Class<X> filter){
		var c = childrenOfType(parent, filter);
		if(c.size() > 0)
			return Optional.of(c.get(0));
		else
			return Optional.empty();
	}
}