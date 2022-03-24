package cyclic.intellij.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.SuggestedNameInfo;
import com.intellij.refactoring.rename.NameSuggestionProvider;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.text.NameUtilCore;
import cyclic.intellij.psi.CycDefinition;
import cyclic.intellij.psi.CycVarScope;
import cyclic.intellij.psi.CycVariable;
import cyclic.intellij.psi.utils.JvmClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

public class CycNameSuggestionProvider implements NameSuggestionProvider{
	
	// TODO: centralise keyword names
	// TODO: remove strictfp when compiler does
	public static final Set<String> ILLEGAL_NAMES = Set.of(
			"boolean", "byte", "short", "char", "int", "long", "float", "double", "var", "val", "void",
			"public", "private", "protected", "static", "volatile", "native", "abstract", "synchronised", "strictfp", "sealed",
			"class", "interface", "enum", "record", "single",
			"import", "package", "extends", "implements", "permits",
			"this", "super", "instanceof", "null", "true", "false",
			"default", "switch", "case", "for", "while", "do", "if", "throw", "return", "yield"
	);
	
	public static final Set<Character> VOWELS = Set.of('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U');
	
	public @Nullable SuggestedNameInfo getSuggestedNames(@NotNull PsiElement element,
	                                                     @Nullable PsiElement context,
	                                                     @NotNull Set<String> result){
		if(!(element instanceof CycDefinition))
			return null;
		CycDefinition target = (CycDefinition)element;
		var collected = new ArrayList<String>();
		
		var shortName = target.getName();
		collected.add(shortName);
		collected.add(shortName.toLowerCase(Locale.ROOT));
		
		var words = NameUtilCore.nameToWords(shortName);
		collected.add(joinCamelCase(words));
		collected.add(joinSnakeCase(words));
		
		if(target instanceof CycVariable){
			CycVariable variable = (CycVariable)target;
			var typeName = NameUtilCore.nameToWords(JvmClassUtils.name(variable.varType()).replace("[", "").replace("]", ""));
			collected.add(joinCamelCase(typeName));
			collected.add(joinSnakeCase(typeName));
			if(variable.hasModifier("static") && variable.hasModifier("final")){
				collected.add(joinSnakeCase(words).toUpperCase(Locale.ROOT));
				collected.add(joinSnakeCase(typeName).toUpperCase(Locale.ROOT));
			}
		}
		
		var adjusted = new ArrayList<String>(collected.size());
		var scope = CycVarScope.scopeOf(element).orElse(null);
		Predicate<String> inScope = y -> scope != null && scope.available().stream().map(CycVariable::varName).anyMatch(x -> x.equals(y));
		for(String found : collected){
			String adj = found;
			if(!adj.equals(shortName))
				adj = adj.replaceAll("_+", "_");
			if(ILLEGAL_NAMES.contains(found)){
				var rest = found.contains("_")
						? "_" + found
						: (found.substring(0, 1).toUpperCase(Locale.ROOT) + found.substring(1));
				adj = VOWELS.contains(found.charAt(0)) ? "an" + rest : "a" + rest;
			}
			if(!adj.equals(shortName) && inScope.test(adj))
				for(int i = 2; i < Integer.MAX_VALUE; i++)
					if(!inScope.test(adj + i)){
						adj = adj + i;
						break;
					}
			adjusted.add(adj);
		}
		
		ContainerUtil.removeDuplicates(adjusted);
		result.addAll(adjusted);
		// don't care about which suggestions are accepted
		return null;
	}
	
	public static String joinCamelCase(String[] words){
		var builder = new StringBuilder();
		for(int i = 0; i < words.length; i++){
			String word = words[i];
			if(i == 0)
				builder.append(word.toLowerCase(Locale.ROOT));
			else{
				builder.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
				builder.append(word.substring(1).toLowerCase(Locale.ROOT));
			}
		}
		return builder.toString();
	}
	
	public static String joinSnakeCase(String[] words){
		var builder = new StringBuilder();
		for(int i = 0; i < words.length; i++){
			String word = words[i];
			if(i == 0)
				builder.append(word.toLowerCase(Locale.ROOT));
			else{
				builder.append("_");
				builder.append(word.toLowerCase(Locale.ROOT));
			}
		}
		return builder.toString();
	}
}