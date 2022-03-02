package cyclic.intellij.presentation;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.daemon.NavigateAction;
import com.intellij.codeInsight.daemon.impl.MarkerType;
import com.intellij.icons.AllIcons;
import com.intellij.java.JavaBundle;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import cyclic.intellij.presentation.find.CMarkerTypes;
import cyclic.intellij.psi.elements.CycMethod;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CyclicLineMarkerProvider implements LineMarkerProvider{
	
	public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element){
		if(element instanceof CycMethod){
			var overrides = ((CycMethod)element).overriddenMethod();
			if(overrides != null){
				boolean isImplement = overrides.hasModifier(JvmModifier.ABSTRACT);
				MarkerType type = CMarkerTypes.CYC_OVERRIDING_METHOD;
				var id = ((CycMethod)element).getNameIdentifier();
				var name = id != null ? id.getFirstChild() : null;
				Icon icon = isImplement ? AllIcons.Gutter.ImplementingMethod : AllIcons.Gutter.OverridingMethod;
				if(name != null){
					var info = new LineMarkerInfo<>(name,
							name.getTextRange(),
							icon, type.getTooltip(),
							type.getNavigationHandler(),
							GutterIconRenderer.Alignment.RIGHT,
							name::getText);
					info = NavigateAction.setNavigateAction(info, JavaBundle.message("action.go.to.super.method.text"), IdeActions.ACTION_GOTO_SUPER);
					return info;
				}
			}
		}
		return null;
	}
}