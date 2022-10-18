package cyclic.intellij.presentation.structureView;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicStructViewFactory implements PsiStructureViewFactory{
	
	public @Nullable StructureViewBuilder getStructureViewBuilder(@NotNull PsiFile psiFile){
		return new TreeBasedStructureViewBuilder(){
			public @NotNull StructureViewModel createStructureViewModel(@Nullable Editor editor){
				return new CyclicStructViewModel(psiFile, editor);
			}
			
			public boolean isRootNodeShown(){
				return false;
			}
		};
	}
}
