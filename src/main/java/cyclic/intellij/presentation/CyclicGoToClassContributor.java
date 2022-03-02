package cyclic.intellij.presentation;

import com.intellij.lang.Language;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmElement;
import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.psi.elements.CycType;
import cyclic.intellij.psi.utils.ProjectTypeFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicGoToClassContributor implements GotoClassContributor{
	
	public @Nullable String getQualifiedName(NavigationItem item){
		if(item instanceof CycType)
			return ((CycType)item).fullyQualifiedName();
		return null;
	}
	
	public @Nullable String getQualifiedNameSeparator(){
		return ".";
	}
	
	public @Nullable Language getElementLanguage(){
		return CyclicLanguage.LANGUAGE;
	}
	
	public String @NotNull [] getNames(Project project, boolean includeNonProjectItems){
		return ProjectTypeFinder.findAll(project, type -> true).stream()
				.map(JvmClass::getQualifiedName)
				.toArray(String[]::new);
	}
	
	public NavigationItem @NotNull [] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems){
		return ProjectTypeFinder.findAll(project, type -> type.getName().contains(pattern)).stream()
				.map(JvmElement::getSourceElement)
				.filter(NavigationItem.class::isInstance)
				.toArray(NavigationItem[]::new);
	}
}