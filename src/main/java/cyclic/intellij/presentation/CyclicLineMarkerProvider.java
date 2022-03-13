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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.light.LightMethodBuilder;
import com.intellij.psi.search.searches.DirectClassInheritorsSearch;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import cyclic.intellij.asJvm.AsPsiUtil;
import cyclic.intellij.presentation.find.CMarkerTypes;
import cyclic.intellij.psi.ast.CycCall;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.expressions.CycIdExpr;
import cyclic.intellij.psi.ast.expressions.CycThisExpr;
import cyclic.intellij.psi.ast.statements.CycStatement;
import cyclic.intellij.psi.ast.types.CycType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
			if(element instanceof CycType){
				CycType type = (CycType)element;
				PsiClass aClass = AsPsiUtil.asPsiClass(type);
				PsiClass subClass = DirectClassInheritorsSearch.search(aClass).findFirst();
				if(subClass != null){
					final Icon icon = aClass.isInterface() ? AllIcons.Gutter.ImplementedMethod : AllIcons.Gutter.OverridenMethod;
					PsiElement name = type.getNameIdentifier();
					if(name != null)
						name = name.getFirstChild();
					if(name == null)
						name = aClass;
					MarkerType marker = CMarkerTypes.CYC_SUBCLASSED_CLASS;
					LineMarkerInfo<PsiElement> info = new LineMarkerInfo<>(name, name.getTextRange(),
							icon, marker.getTooltip(),
							marker.getNavigationHandler(),
							GutterIconRenderer.Alignment.RIGHT,
							type::getName);
					NavigateAction.setNavigateAction(info,
							aClass.isInterface()
									? JavaBundle.message("action.go.to.implementation.text") : JavaBundle.message("action.go.to.subclass.text"),
							IdeActions.ACTION_GOTO_IMPLEMENTATION);
					result.add(info);
				}
			}
			if(element instanceof CycMethod){
				CycMethod cMethod = (CycMethod)element;
				PsiMethod method = AsPsiUtil.asPsiMethod(cMethod);
				PsiClass container = AsPsiUtil.asPsiClass(cMethod.containingType());
				// TODO: don't build the type twice
				// we do need to set the container or the query won't bother
				((LightMethodBuilder)method).setContainingClass(container);
				
				PsiMethod overridden = OverridingMethodsSearch.search(method).findFirst();
				if(overridden != null){
					var abs = cMethod.hasModifier("abstract") || (container.isInterface() && cMethod.hasSemicolon());
					final Icon icon = abs
							? AllIcons.Gutter.ImplementedMethod : AllIcons.Gutter.OverridenMethod;
					PsiElement name = cMethod.getNameIdentifier();
					if(name != null)
						name = name.getFirstChild();
					if(name == null)
						name = method;
					MarkerType marker = CMarkerTypes.CYC_OVERRIDDEN_METHOD;
					LineMarkerInfo<PsiElement> info = new LineMarkerInfo<>(name, name.getTextRange(),
							icon, marker.getTooltip(),
							marker.getNavigationHandler(),
							GutterIconRenderer.Alignment.RIGHT,
							cMethod::getName);
					NavigateAction.setNavigateAction(info,
							abs ? JavaBundle.message("action.go.to.implementation.text") : JavaBundle.message("action.go.to.subclass.text"),
							IdeActions.ACTION_GOTO_IMPLEMENTATION);
					result.add(info);
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
					__ -> JavaBundle.message("tooltip.recursive.call"),
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