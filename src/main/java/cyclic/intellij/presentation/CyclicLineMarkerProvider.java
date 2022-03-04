package cyclic.intellij.presentation;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.daemon.NavigateAction;
import com.intellij.codeInsight.daemon.impl.MarkerType;
import com.intellij.icons.AllIcons;
import com.intellij.java.JavaBundle;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import cyclic.intellij.presentation.find.CMarkerTypes;
import cyclic.intellij.psi.elements.CycCall;
import cyclic.intellij.psi.elements.CycMethod;
import cyclic.intellij.psi.elements.CycStatement;
import cyclic.intellij.psi.elements.CycType;
import cyclic.intellij.psi.expressions.CycIdExpr;
import cyclic.intellij.psi.expressions.CycThisExpr;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;

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
	
	public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result){
		Set<CycStatement> visited = new HashSet<>();
		
		for(PsiElement element : elements){
			ProgressManager.checkCanceled();
			if(element instanceof CycCall){
				CycCall call = (CycCall)element;
				var statement = PsiTreeUtil.getParentOfType(call, CycStatement.class, true, CycMethod.class);
				if(!visited.contains(statement) && isRecursiveCall(call)){
					visited.add(statement);
					ContainerUtil.addIfNotNull(result, RecursiveCallMarkerInfo.create(call));
				}
			}
		}
	}
	
	protected boolean isRecursiveCall(CycCall call){
		var on = call.getOn();
		if(on != null && !(on instanceof CycThisExpr) && !(on instanceof CycIdExpr && ((CycIdExpr)on).on() == null))
			return false;
		
		var name = call.getMethodName();
		var method = PsiTreeUtil.getParentOfType(call, CycMethod.class, true, CycType.class);
		if(method == null || name == null || !method.getName().equals(name.getText()))
			return false;
		
		return call.isReferenceTo(method);
	}
	
	private static final class RecursiveCallMarkerInfo extends LineMarkerInfo<PsiElement>{
		private static RecursiveCallMarkerInfo create(@NotNull CycCall call){
			PsiElement name = call.getMethodName();
			if(name != null)
				return new RecursiveCallMarkerInfo(name);
			return null;
		}
		
		private RecursiveCallMarkerInfo(@NotNull PsiElement name){
			super(name,
					name.getTextRange(),
					AllIcons.Gutter.RecursiveMethod,
					__ -> (JavaBundle.message("tooltip.recursive.call")),
					null,
					GutterIconRenderer.Alignment.RIGHT,
					name::getText);
		}
		
		@Override
		public GutterIconRenderer createGutterRenderer(){
			if(myIcon == null)
				return null;
			return new LineMarkerGutterIconRenderer<>(this){
				@Override
				public AnAction getClickAction(){
					return null;
				}
			};
		}
	}
}