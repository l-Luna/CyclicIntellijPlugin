package cyclic.intellij.presentation;

import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import cyclic.intellij.psi.CycDefinition;
import cyclic.intellij.psi.ast.CycIdPart;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.types.JvmCyclicMethod;
import cyclic.intellij.psi.utils.JvmClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicRunLineMarkerContributor extends RunLineMarkerContributor{
	
	public @Nullable Info getInfo(@NotNull PsiElement element){
		element = element.getParent(); // ID token -> CycIdPart
		element = element instanceof CycIdPart ? element.getParent() : null; // CycIdPart -> CycDefinition
		if(element instanceof CycDefinition){
			if((element instanceof CycMethod && JvmClassUtils.isMainMethod(JvmCyclicMethod.of((CycMethod)element)))
				|| (element instanceof CycType && JvmClassUtils.hasMainMethod(JvmCyclicClass.of((CycType)element)))){
				AnAction[] actions = ExecutorAction.getActions(Integer.MAX_VALUE);
				return new Info(
						AllIcons.RunConfigurations.TestState.Run,
						actions,
						x -> StringUtil.join(ContainerUtil.mapNotNull(actions, action -> getText(action, x.getParent())), "\n")
				);
			}
		}
		return null;
	}
}