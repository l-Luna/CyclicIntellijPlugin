package cyclic.intellij.psi.utils;

import com.intellij.lang.jvm.JvmMethod;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.tree.IElementType;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.Tokens;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PsiUtils{
	
	@NotNull
	private static PsiElement createFromText(Project project, PsiElement context, String text, IElementType type){
		PsiFileFactoryImpl factory = (PsiFileFactoryImpl)PsiFileFactory.getInstance(project);
		var ret = factory.createElementFromText(text, CyclicLanguage.LANGUAGE, type, context);
		assert ret != null;
		return ret;
	}
	
	@NotNull
	public static PsiElement createFromText(@NotNull PsiElement context, String text, IElementType elementType){
		return createFromText(context.getProject(), context, text, elementType).getFirstChild();
	}
	
	// the ID token is understood in ParserAdaptor to mean a full ID
	@NotNull
	public static PsiElement createIdFromText(@NotNull PsiElement context, String text){
		return createFromText(context, text, Tokens.getFor(CyclicLangLexer.ID));
	}
	
	@NotNull
	public static PsiElement createIdPartFromText(@NotNull PsiElement context, String text){
		return createIdFromText(context, text).getFirstChild();
	}
	
	@NotNull
	public static PsiElement createExpressionFromText(@NotNull PsiElement context, String text){
		return createFromText(context, text, Tokens.getRuleFor(CyclicLangParser.RULE_value));
	}
	
	@NotNull
	public static PsiElement createImportFromText(@NotNull PsiElement context, String text){
		return createFromText(context, text, Tokens.getRuleFor(CyclicLangParser.RULE_importDecl));
	}
	
	@NotNull
	public static PsiElement createExtendsClauseFromText(@NotNull PsiElement context, String text){
		return createFromText(context, text, Tokens.getRuleFor(CyclicLangParser.RULE_objectExtends));
	}
	
	@NotNull
	public static PsiElement createImplementsClauseFromText(@NotNull PsiElement context, String text){
		return createFromText(context, text, Tokens.getRuleFor(CyclicLangParser.RULE_objectImplements));
	}
	
	@NotNull
	public static PsiElement createTypeReferenceFromText(@NotNull PsiElement context, String text){
		return createFromText(context, text, Tokens.getRuleFor(CyclicLangParser.RULE_type));
	}
	
	@NotNull
	public static PsiElement createMemberDefinitionFromText(@NotNull PsiElement context, String text){
		return createFromText(context, text, Tokens.getRuleFor(CyclicLangParser.RULE_member));
	}
	
	@NotNull
	public static PsiElement createWhitespace(@NotNull PsiElement context, String text){
		assert text.isBlank();
		return createFromText(context, text, Tokens.getFor(CyclicLangLexer.ID));
	}
	
	@NotNull
	public static <X> List<X> childrenOfType(@NotNull PsiElement parent, Class<X> filter){
		return streamChildrenOfType(parent, filter)
				.collect(Collectors.toList());
	}
	
	@NotNull
	public static <X> Stream<X> streamChildrenOfType(@NotNull PsiElement parent, Class<X> filter){
		return Arrays.stream(parent.getChildren())
				.filter(filter::isInstance)
				.map(z -> (X)z);
	}
	
	@NotNull
	public static <X> List<X> wrappedChildrenOfType(@NotNull PsiElement parent, Class<X> filter){
		return streamWrappedChildrenOfType(parent, filter)
				.collect(Collectors.toList());
	}
	
	@NotNull
	public static <X> Stream<X> streamWrappedChildrenOfType(@NotNull PsiElement parent, Class<X> filter){
		return Arrays.stream(parent.getChildren())
				.map(PsiElement::getFirstChild)
				.filter(filter::isInstance)
				.map(z -> (X)z);
	}
	
	@NotNull
	public static List<PsiElement> matchingChildren(@NotNull PsiElement parent, Predicate<PsiElement> filter){
		return Arrays.stream(parent.getChildren())
				.filter(filter)
				.collect(Collectors.toList());
	}
	
	@NotNull
	public static <X extends PsiElement> Optional<X> childOfType(@NotNull PsiElement parent, Class<X> filter){
		return streamChildrenOfType(parent, filter).findFirst();
	}
	
	@NotNull
	public static <X extends PsiElement> Optional<X> childOfType(@NotNull PsiElement parent, Class<X> filter, int index){
		return streamChildrenOfType(parent, filter)
				.skip(index)
				.findFirst();
	}
	
	@NotNull
	public static <X extends PsiElement> Optional<X> wrappedChildOfType(@NotNull PsiElement parent, Class<X> filter){
		return streamWrappedChildrenOfType(parent, filter).findFirst();
	}
	
	@NotNull
	public static <X extends PsiElement> Optional<X> wrappedChildOfType(@NotNull PsiElement parent, Class<X> filter, int index){
		return streamWrappedChildrenOfType(parent, filter)
				.skip(index)
				.findFirst();
	}
	
	public static PsiElement generateMethodPrototype(JvmMethod from, PsiElement context){
		/* type name(params){ return (null | 0 | false)?; } */
		StringBuilder template = new StringBuilder();
		JvmType type = from.getReturnType();
		template.append(JvmClassUtils.name(type));
		template.append(" ");
		template.append(from.getName());
		template.append("(");
		JvmParameter @NotNull [] parameters = from.getParameters();
		for(int i = 0; i < parameters.length; i++){
			JvmParameter parameter = parameters[i];
			if(i != 0)
				template.append(", ");
			template.append(JvmClassUtils.name(parameter.getType()));
			template.append(" ");
			template.append(parameter.getName());
		}
		template.append(")");
		template.append("{\n\t");
		if(Objects.equals(type, PsiPrimitiveType.VOID))
			;
		else if(Objects.equals(type, PsiPrimitiveType.BOOLEAN))
			template.append("return false;");
		else if(type instanceof PsiPrimitiveType && !Objects.equals(type, PsiPrimitiveType.NULL))
			template.append("return 0;");
		else
			template.append("return null;");
		template.append("\n}");
		
		return createMemberDefinitionFromText(context, template.toString());
	}
}