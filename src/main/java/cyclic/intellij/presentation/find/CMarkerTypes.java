package cyclic.intellij.presentation.find;

import com.intellij.codeInsight.daemon.impl.GutterTooltipHelper;
import com.intellij.codeInsight.daemon.impl.LineMarkerNavigator;
import com.intellij.codeInsight.daemon.impl.MarkerType;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.elements.CycMethod;

import java.awt.event.MouseEvent;
import java.util.List;

public final class CMarkerTypes{
	
	public static final MarkerType CYC_OVERRIDING_METHOD = new MarkerType(
			"CYC_OVERRIDING_METHOD",
			id -> {
				var par = id.getParent().getParent();
				if(par instanceof CycMethod){
					var overrides = ((CycMethod)par).overriddenMethod();
					if(overrides == null || overrides.getSourceElement() == null)
						return null;
					boolean implementing = overrides.hasModifier(JvmModifier.ABSTRACT);
					return GutterTooltipHelper.getTooltipText(
							List.of(overrides.getSourceElement()),
							superMethod ->
									(implementing ? "Implements" : "Overrides") + " ",
							superMethod -> false,
							IdeActions.ACTION_GOTO_SUPER);
				}
				return null;
			},
			new LineMarkerNavigator(){
				public void browse(MouseEvent e, PsiElement id){
					var par = id.getParent().getParent();
					if(par instanceof CycMethod){
						var overrides = ((CycMethod)par).overriddenMethod();
						if(overrides == null || overrides.getSourceElement() == null)
							return;
						var element = overrides.getSourceElement();
						if(element instanceof NavigationItem)
							((NavigationItem)element).navigate(true);
					}
				}
			}
	);
}