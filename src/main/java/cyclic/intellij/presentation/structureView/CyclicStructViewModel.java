package cyclic.intellij.presentation.structureView;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.java.VisibilitySorter;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.ast.CycConstructor;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.common.CycVariableDef;
import cyclic.intellij.psi.ast.types.CycType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicStructViewModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider{
	
	public CyclicStructViewModel(@NotNull PsiFile psiFile, @Nullable Editor editor){
		super(psiFile, editor, new CyclicStructViewElement(psiFile));
	}
	
	public Sorter @NotNull [] getSorters(){
		return new Sorter[]{ VisibilitySorter.INSTANCE, Sorter.ALPHA_SORTER };
	}
	
	public boolean isAlwaysShowsPlus(StructureViewTreeElement element){
		return false;
	}
	
	public boolean isAlwaysLeaf(StructureViewTreeElement element){
		return false;
	}
	
	protected Class<?> @NotNull [] getSuitableClasses(){
		return new Class[]{ CycFile.class, CycType.class, CycMethod.class, CycVariableDef.class, CycConstructor.class };
	}
}