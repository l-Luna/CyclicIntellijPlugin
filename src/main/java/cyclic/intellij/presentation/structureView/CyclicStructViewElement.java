package cyclic.intellij.presentation.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.structureView.impl.java.AccessLevelProvider;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import cyclic.intellij.psi.CycCodeHolder;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.CycVariable;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.types.JvmCyclicField;
import cyclic.intellij.psi.utils.JvmClassUtils;
import cyclic.intellij.psi.utils.Visibility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CyclicStructViewElement extends PsiTreeElementBase<PsiElement> implements SortableTreeElement, AccessLevelProvider{
	
	protected CyclicStructViewElement(PsiElement psiElement){
		super(psiElement);
	}
	
	public @NotNull String getAlphaSortKey(){
		var name = getElement() instanceof PsiNamedElement named ? named.getName() : null;
		return name != null ? name : "";
	}
	
	public @NotNull Collection<StructureViewTreeElement> getChildrenBase(){
		if(getElement() instanceof CycFile file)
			return file.getTypeDef()
					.map(x -> List.<StructureViewTreeElement>of(new CyclicStructViewElement(x)))
					.orElse(List.of());
		if(getElement() instanceof CycType type){
			return type.getMembers().stream()
					.map(PsiElement::getFirstChild)
					.filter(NavigatablePsiElement.class::isInstance)
					.map(NavigatablePsiElement.class::cast)
					.<StructureViewTreeElement>map(CyclicStructViewElement::new)
					.toList();
		}
		
		return List.of();
	}
	
	public int getAccessLevel(){
		var element = getElement();
		return element instanceof CycCodeHolder cc ? Visibility.getVisibilityLevel(cc.toJvm()) :
				element instanceof CycVariable v ? Visibility.getVisibilityLevel(JvmCyclicField.of(v)) :
				element instanceof CycType t ? Visibility.getVisibilityLevel(JvmCyclicClass.of(t)) :
				0;
	}
	
	public int getSubLevel(){
		return 0;
	}
	
	public @NlsSafe @Nullable String getPresentableText(){
		var element = getElement();
		boolean dumb = element == null || DumbService.isDumb(element.getProject());
		if(element instanceof CycMethod method){
			String args = method.parameters().stream().map(x -> dumb ? x.getName() : JvmClassUtils.name(x.varType())).collect(Collectors.joining(", "));
			String ret = dumb ? "" : ": " + JvmClassUtils.name(method.returnType());
			return "%s(%s)%s".formatted(method.getName(), args, ret);
		}
		if(element instanceof CycVariable var && !dumb)
			return var.varName() + ": " + JvmClassUtils.name(var.varType());
		return element instanceof PsiNamedElement named ? named.getName() : null;
	}
}
