package cyclic.intellij.presentation.find;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopeUtil;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.DirectClassInheritorsSearch.SearchParameters;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import cyclic.intellij.asJava.AsPsiUtil;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.JvmClassUtils;
import cyclic.intellij.psi.utils.ProjectTypeFinder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CyclicDirectInheritorsSearcher implements QueryExecutor<PsiClass, SearchParameters>{
	
	// based on the groovy version, but worse!
	public boolean execute(SearchParameters queryParameters, @NotNull Processor<? super PsiClass> consumer){
		var clss = queryParameters.getClassToProcess();
		SearchScope scope = ReadAction.compute(() -> queryParameters.getScope().intersectWith(clss.getUseScope()));
		Project project = PsiUtilCore.getProjectInReadAction(clss);
		GlobalSearchScope globalSearchScope = GlobalSearchScopeUtil.toGlobalSearchScope(scope, project);
		DumbService dumbService = DumbService.getInstance(project);
		List<PsiClass> candidates = dumbService.runReadActionInSmartMode(() -> {
			if(!clss.isValid())
				return Collections.emptyList();
			return getDerivingClassCandidates(clss, globalSearchScope);
		});
		
		if(!candidates.isEmpty())
			for(PsiClass candidate : candidates)
				if(!consumer.process(candidate))
					return false;
		return true;
	}
	
	private List<PsiClass> getDerivingClassCandidates(PsiClass clss, GlobalSearchScope scope){
		var name = clss.getName();
		if(name == null)
			return Collections.emptyList();
		
		ArrayList<PsiClass> inheritors = new ArrayList<>();
		// TODO: non-project inheritors!
		Project project = scope.getProject();
		if(project == null)
			return List.of();
		for(JvmClass aClass : ProjectTypeFinder.findAll(project, x -> true, scope))
			if(aClass instanceof JvmCyclicClass){
				var underlying = ((JvmCyclicClass)aClass).getUnderlying();
				if(JvmClassUtils.isClassAssignableTo(aClass, clss) && !Objects.equals(aClass.getQualifiedName(), clss.getQualifiedName()))
					inheritors.add(AsPsiUtil.asPsiClass(underlying));
			}
		return inheritors;
	}
}