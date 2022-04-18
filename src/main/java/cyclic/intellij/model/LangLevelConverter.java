package cyclic.intellij.model;

import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LangLevelConverter extends Converter<CyclicLanguageLevel>{
	
	public @Nullable CyclicLanguageLevel fromString(@NotNull String value){
		return CyclicLanguageLevel.getById(value);
	}
	
	public @Nullable String toString(@NotNull CyclicLanguageLevel value){
		return value.getId();
	}
}