package cyclic.intellij.refactoring.generation;

import com.intellij.codeInsight.generation.ClassMemberWithElement;
import com.intellij.codeInsight.generation.MemberChooserObject;
import com.intellij.codeInsight.generation.PsiElementMemberChooserObject;
import com.intellij.ide.util.MemberChooser;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiElement;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.JvmClassUtils;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class CycOverrideImplementMemberChooser{
	
	public static MemberChooser<JvmMethodMember> create(CycType type){
		List<JvmMethod> choices = JvmClassUtils.findUnimplementedMethodsFrom(JvmCyclicClass.of(type), false, false);
		List<JvmMethodMember> members = choices.stream()
				.map(x -> new JvmMethodMember(x, x.getSourceElement()))
				.collect(Collectors.toList());
		
		MemberChooser<JvmMethodMember> chooser
				= new MemberChooser<>(members.toArray(JvmMethodMember.BUILDER), false, true, type.getProject());
		chooser.setTitle(CyclicBundle.message("chooser.title.overrideImplement"));
		
		JvmMethodMember[] selected = members.stream()
				.filter(x -> x.method.hasModifier(JvmModifier.ABSTRACT))
				.toArray(JvmMethodMember.BUILDER);
		if(selected.length > 0)
			chooser.selectElements(selected);
		
		return chooser;
	}
	
	public static MemberChooser<JvmMethodMember> show(CycType type){
		MemberChooser<JvmMethodMember> chooser = create(type);
		chooser.show();
		if(chooser.getExitCode() != DialogWrapper.OK_EXIT_CODE)
			return null;
		return chooser;
	}
	
	public static List<JvmMethodMember> select(CycType type){
		MemberChooser<JvmMethodMember> chooser = show(type);
		if(chooser == null)
			return List.of();
		
		List<JvmMethodMember> selected = chooser.getSelectedElements();
		if(selected == null)
			return List.of();
		return selected;
	}
	
	public static class JvmMethodMember extends PsiElementMemberChooserObject implements ClassMemberWithElement{
		
		public static final IntFunction<JvmMethodMember[]> BUILDER = JvmMethodMember[]::new;
		
		public JvmMethod method;
		private PsiElement source;
		
		public JvmMethodMember(JvmMethod method, PsiElement source){
			super(source, JvmClassUtils.summary(method), source.getIcon(0));
			this.method = method;
			this.source = source;
		}
		
		public PsiElement getElement(){
			return source;
		}
		
		public MemberChooserObject getParentNodeDelegate(){
			var container = method.getContainingClass();
			assert container != null;
			PsiElement source = container.getSourceElement();
			assert source != null;
			return new PsiElementMemberChooserObject(source, container.getQualifiedName(), source.getIcon(0));
		}
	}
}