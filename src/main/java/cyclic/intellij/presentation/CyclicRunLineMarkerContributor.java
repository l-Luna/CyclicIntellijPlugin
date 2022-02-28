package cyclic.intellij.presentation;

import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmElement;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import cyclic.intellij.psi.CycElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.lang.jvm.source.JvmDeclarationSearch.getElementsByIdentifier;
import static com.intellij.lang.jvm.util.JvmMainMethodUtil.hasMainMethodInHierarchy;
import static com.intellij.lang.jvm.util.JvmMainMethodUtil.isMainMethod;

public class CyclicRunLineMarkerContributor extends RunLineMarkerContributor{
	
	public @Nullable Info getInfo(@NotNull PsiElement element){
		// this is exactly what JvmApplicationRunLineMarkerContributor does,
		// but it's disabled by default
		if(element instanceof CycElement){
			for(JvmElement declaration : getElementsByIdentifier(element)){
				if((declaration instanceof JvmMethod && isMainMethod((JvmMethod)declaration))
						|| (declaration instanceof JvmClass && hasMainMethodInHierarchy((JvmClass)declaration))){
					AnAction[] actions = ExecutorAction.getActions(Integer.MAX_VALUE);
					return new Info(
							AllIcons.RunConfigurations.TestState.Run,
							actions,
							x -> StringUtil.join(ContainerUtil.mapNotNull(actions, action -> getText(action, x)), "\n")
					);
				}
			}
		}
		return null;
	}
}