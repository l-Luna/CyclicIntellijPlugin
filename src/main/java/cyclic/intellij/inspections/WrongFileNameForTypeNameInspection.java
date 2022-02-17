package cyclic.intellij.inspections;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import cyclic.intellij.inspections.fixes.RenameFileToTypeFix;
import cyclic.intellij.inspections.fixes.RenameTypeToFileFix;
import cyclic.intellij.psi.CycType;
import org.jetbrains.annotations.NotNull;

public class WrongFileNameForTypeNameInspection extends CyclicInspection{
	
	public ProblemDescriptor @NotNull [] checkTypeDef(@NotNull CycType type, @NotNull InspectionManager manager, boolean isOnTheFly){
		String typeName = type.getName();
		if(type.isTopLevelType() && type.getNameIdentifier() != null){
			String filename = type.getContainingFile().getName();
			// TODO: check for non-standard file extensions
			String expected = filename.substring(0, filename.length() - 4);
			if(!typeName.equals(expected))
				return new ProblemDescriptor[]{ manager.createProblemDescriptor(type.getNameIdentifier(), "Cyclic type '" + typeName + "' should be declared in file '" + typeName + ".cyc'", new LocalQuickFix[]{ new RenameFileToTypeFix(type, typeName), new RenameTypeToFileFix(type, expected) }, ProblemHighlightType.ERROR, isOnTheFly, false) };
		}
		return super.checkTypeDef(type, manager, isOnTheFly);
	}
	
	public boolean isEnabledByDefault(){
		return true;
	}
	
	@NotNull
	public HighlightDisplayLevel getDefaultLevel() {
		return HighlightDisplayLevel.ERROR;
	}
	
	@NotNull
	public String getShortName(){
		return "WrongFileNameForTypeName";
	}
}