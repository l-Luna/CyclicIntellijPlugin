package cyclic.intellij.presentation.find;

import com.intellij.codeInsight.daemon.impl.GutterTooltipHelper;
import com.intellij.codeInsight.daemon.impl.LineMarkerNavigator;
import com.intellij.codeInsight.daemon.impl.MarkerType;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.light.LightMethodBuilder;
import cyclic.intellij.asJvm.AsPsiUtil;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.types.CycType;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
	
	public static final MarkerType CYC_SUBCLASSED_CLASS = new MarkerType(
			"CYC_SUBCLASSED_CLASS",
			id -> {
				var par = id.getParent().getParent();
				if(par instanceof CycType){
					CycType type = (CycType)par;
					PsiClass asPsi = AsPsiUtil.asPsiClass(type);
					return MarkerType.getSubclassedClassTooltip(asPsi);
				}
				return null;
			},
			new LineMarkerNavigator(){
				public void browse(MouseEvent e, PsiElement id){
					var par = id.getParent().getParent();
					if(par instanceof CycType){
						CycType type = (CycType)par;
						PsiClass asPsi = AsPsiUtil.asPsiClass(type);
						MarkerType.navigateToSubclassedClass(e, asPsi);
					}
				}
			}
	);
	
	public static final MarkerType CYC_OVERRIDDEN_METHOD = new MarkerType(
			"CYC_OVERRIDDEN_METHOD",
			id -> {
				var par = id.getParent().getParent();
				if(par instanceof CycMethod){
					CycMethod cycMethod = (CycMethod)par;
					PsiMethod asPsi = AsPsiUtil.asPsiMethod(cycMethod);
					// TODO: see CyclicLineMarkerProvider
					//   don't build method twice
					PsiClass container = AsPsiUtil.asPsiClass(cycMethod.containingType());
					((LightMethodBuilder)asPsi).setContainingClass(container);
					return getOverriddenMethodTooltip(asPsi);
				}
				return null;
			},
			new LineMarkerNavigator(){
				public void browse(MouseEvent e, PsiElement id){
					var par = id.getParent().getParent();
					if(par instanceof CycMethod){
						CycMethod cycMethod = (CycMethod)par;
						PsiMethod asPsi = AsPsiUtil.asPsiMethod(cycMethod);
						// see above
						PsiClass container = AsPsiUtil.asPsiClass(cycMethod.containingType());
						((LightMethodBuilder)asPsi).setContainingClass(container);
						navigateToOverriddenMethod(e, asPsi);
					}
				}
			}
	);
	
	// access by reflection
	public static final Method OVERRIDE_METHOD_TOOLTIP, OVERRIDE_METHOD_NAVIGATE;
	
	static{
		try{
			OVERRIDE_METHOD_TOOLTIP =
					MarkerType.class.getDeclaredMethod("getOverriddenMethodTooltip", PsiMethod.class);
			OVERRIDE_METHOD_NAVIGATE =
					MarkerType.class.getDeclaredMethod("navigateToOverriddenMethod", MouseEvent.class, PsiMethod.class);
		}catch(NoSuchMethodException e){
			throw new RuntimeException(e);
		}
	}
	
	private static String getOverriddenMethodTooltip(@NotNull PsiMethod method){
		try{
			OVERRIDE_METHOD_TOOLTIP.setAccessible(true);
			String ret = (String)OVERRIDE_METHOD_TOOLTIP.invoke(null, method);
			OVERRIDE_METHOD_TOOLTIP.setAccessible(false);
			return ret;
		}catch(IllegalAccessException | InvocationTargetException e){
			throw new RuntimeException(e);
		}
	}
	
	private static void navigateToOverriddenMethod(MouseEvent e, @NotNull final PsiMethod method){
		try{
			OVERRIDE_METHOD_NAVIGATE.setAccessible(true);
			OVERRIDE_METHOD_NAVIGATE.invoke(null, e, method);
			OVERRIDE_METHOD_NAVIGATE.setAccessible(false);
		}catch(IllegalAccessException | InvocationTargetException ex){
			throw new RuntimeException(ex);
		}
	}
}