package cyclic.intellij.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.tree.IElementType;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
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
	
	public static PsiElement createIdFromText(@NotNull PsiElement context, String text){
		return createFromText(context, text, Tokens.getFor(CyclicLangLexer.ID)).getFirstChild();
	}
	
	@NotNull public static <x> List<x> childrenOfType(@NotNull PsiElement element, Class<x> filter){
		return Arrays.stream(element.getChildren())
				.filter(filter::isInstance)
				.map(z -> (x)z)
				.collect(Collectors.toList());
	}
	
	@NotNull public static <x> List<x> wrappedChildrenOfType(@NotNull PsiElement element, Class<x> filter){
		return Arrays.stream(element.getChildren())
				.map(PsiElement::getFirstChild)
				.filter(filter::isInstance)
				.map(z -> (x)z)
				.collect(Collectors.toList());
	}
	
	@NotNull public static List<PsiElement> matchingChildren(@NotNull PsiElement element, Predicate<PsiElement> filter){
		return Arrays.stream(element.getChildren())
				.filter(filter)
				.collect(Collectors.toList());
	}
}