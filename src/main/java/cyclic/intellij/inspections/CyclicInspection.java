package cyclic.intellij.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiFile;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.CycType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class CyclicInspection extends LocalInspectionTool{
	
	public ProblemDescriptor @NotNull [] checkTypeDef(@NotNull CycType type, @NotNull InspectionManager manager, boolean isOnTheFly){
		return new ProblemDescriptor[0];
	}
	
	public ProblemDescriptor @NotNull [] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly){
		List<ProblemDescriptor> problems = new ArrayList<>();
		if(file instanceof CycFile){
			CycFile cycFile = (CycFile)file;
			problems.addAll(List.of(cycFile.getTypeDef().map(l -> checkTypeDef(l, manager, isOnTheFly)).orElse(new ProblemDescriptor[0])));
		}
		return problems.toArray(ProblemDescriptor[]::new);
	}
}