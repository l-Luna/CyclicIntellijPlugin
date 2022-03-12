package cyclic.intellij.coverage;

import com.intellij.coverage.*;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.impl.nodes.PackageElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.ui.ColoredTreeCellRenderer;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.ast.types.CycType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicCoverageNodeDecorator extends AbstractCoverageProjectViewNodeDecorator{
	
	public CyclicCoverageNodeDecorator(@NotNull Project project){
		super(project);
	}
	
	public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer){
		PsiElement element = node.getPsiElement();
		if(element == null || !element.isValid() || !(element instanceof CycType || element instanceof CycFile))
			return;
		
		Project project = element.getProject();
		
		CoverageDataManager data = getCoverageDataManager(project);
		JavaCoverageAnnotator annotator = getCovAnnotator(data, project);
		if(annotator == null)
			return;
		
		String fqName = fqName(element);
		if(fqName != null)
			appendCoverageInfo(cellRenderer, annotator.getClassCoverageInformationString(fqName, data));
	}
	
	public void decorate(ProjectViewNode<?> node, PresentationData data){
		Project project = node.getProject();
		if(project == null)
			return;
		
		CoverageDataManager coverageData = getCoverageDataManager(project);
		JavaCoverageAnnotator annotator = getCovAnnotator(coverageData, project);
		if(annotator == null)
			return;
		
		Object value = node.getValue();
		PsiElement element = null;
		if(value instanceof PsiElement)
			element = (PsiElement)value;
		else if(value instanceof SmartPsiElementPointer)
			element = ((SmartPsiElementPointer<?>)value).getElement();
		else if(value instanceof PackageElement){
			PackageElement pkg = (PackageElement)value;
			String coverageString = annotator.getPackageCoverageInformationString(pkg.getPackage(),
					pkg.getModule(),
					coverageData);
			data.setLocationString(coverageString);
		}
		
		if(element instanceof CycType || element instanceof CycFile){
			GlobalSearchScope searchScope = coverageData.getCurrentSuitesBundle().getSearchScope(project);
			VirtualFile vFile = PsiUtilCore.getVirtualFile(element);
			if(vFile != null && searchScope.contains(vFile)){
				String fqName = fqName(element);
				if(fqName != null)
					data.setLocationString(annotator.getClassCoverageInformationString(fqName, coverageData));
			}
		}
	}
	
	@Nullable
	private static JavaCoverageAnnotator getCovAnnotator(@Nullable CoverageDataManager dataManager, @NotNull Project project){
		if(dataManager == null)
			return null;
		
		CoverageSuitesBundle currentSuite = dataManager.getCurrentSuitesBundle();
		if(currentSuite != null){
			final CoverageAnnotator coverageAnnotator = currentSuite.getAnnotator(project);
			if(coverageAnnotator instanceof JavaCoverageAnnotator)
				return (JavaCoverageAnnotator)coverageAnnotator;
		}
		return null;
	}
	
	@Nullable
	private static String fqName(/* CycType | CycFile */ PsiElement element){
		if(element instanceof CycType)
			return ((CycType)element).fullyQualifiedName();
		else
			return ((CycFile)element).getTypeDef().map(CycType::fullyQualifiedName).orElse(null);
	}
}
