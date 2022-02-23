package cyclic.intellij.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.jvm.JvmClassKind;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.lang.jvm.types.JvmReferenceType;
import cyclic.intellij.inspections.fixes.ChangeSupertypeKindFix;
import cyclic.intellij.psi.CycExtendsClause;
import cyclic.intellij.psi.CycImplementsClause;
import cyclic.intellij.psi.CycType;
import cyclic.intellij.psi.CycTypeRef;
import cyclic.intellij.psi.types.CycKind;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InvalidSupertypeInspection extends CyclicInspection{
	
	public ProblemDescriptor @NotNull [] checkTypeDef(@NotNull CycType type, @NotNull InspectionManager manager, boolean isOnTheFly){
		var problems = new ArrayList<ProblemDescriptor>();
		var extendsElems = PsiUtils
				.childOfType(type, CycExtendsClause.class)
				.map(x -> PsiUtils.childrenOfType(x, CycTypeRef.class))
				.orElse(List.of());
		for(CycTypeRef elem : extendsElems){
			if(elem.getTextLength() == 0)
				continue;
			var extType = elem.asType();
			if(extType != null)
				if(!(extType instanceof JvmReferenceType))
					problems.add(manager.createProblemDescriptor(elem, "Expecting a class, not primitive or array", isOnTheFly, new LocalQuickFix[0], ProblemHighlightType.GENERIC_ERROR));
				else{
					var extClass = elem.asClass();
					// TODO: check @AnnotationCanImplement
					if(type.kind() == CycKind.INTERFACE && !(extClass.getClassKind() == JvmClassKind.INTERFACE || extClass.getClassKind() == JvmClassKind.ANNOTATION))
						problems.add(manager.createProblemDescriptor(elem, "Expected an interface, not a class", isOnTheFly, new LocalQuickFix[0], ProblemHighlightType.GENERIC_ERROR));
					else if(type.kind() != CycKind.INTERFACE && (extClass.getClassKind() == JvmClassKind.INTERFACE || extClass.getClassKind() == JvmClassKind.ANNOTATION))
						problems.add(manager.createProblemDescriptor(elem, "Expected a class, not an interface", isOnTheFly, new LocalQuickFix[]{ new ChangeSupertypeKindFix(elem, false) }, ProblemHighlightType.GENERIC_ERROR));
					// TODO: check sealed types
					if(extClass.hasModifier(JvmModifier.FINAL))
						problems.add(manager.createProblemDescriptor(elem, "Cannot extend final type", isOnTheFly, new LocalQuickFix[0], ProblemHighlightType.GENERIC_ERROR));
				}
		}
		var implementsElems = PsiUtils
				.childOfType(type, CycImplementsClause.class)
				.map(x -> PsiUtils.childrenOfType(x, CycTypeRef.class))
				.orElse(List.of());
		for(CycTypeRef elem : implementsElems){
			var extType = elem.asType();
			if(elem.getTextLength() == 0)
				continue;
			if(extType != null)
				if(!(extType instanceof JvmReferenceType))
					problems.add(manager.createProblemDescriptor(elem, "Expecting a class, not primitive or array", isOnTheFly, new LocalQuickFix[0], ProblemHighlightType.GENERIC_ERROR));
				else{
					var implClass = elem.asClass();
					if(type.kind() == CycKind.INTERFACE)
						problems.add(manager.createProblemDescriptor(elem, "Interfaces cannot implement types", isOnTheFly, new LocalQuickFix[0], ProblemHighlightType.GENERIC_ERROR));
					else if(implClass.getClassKind() != JvmClassKind.INTERFACE)
						problems.add(manager.createProblemDescriptor(elem, "Expected an interface", isOnTheFly, new LocalQuickFix[]{ new ChangeSupertypeKindFix(elem, true) }, ProblemHighlightType.GENERIC_ERROR));
					if(implClass.hasModifier(JvmModifier.FINAL)) // is this even valid Java or Cyclic? I guess sealed interfaces are basically this...
						problems.add(manager.createProblemDescriptor(elem, "Cannot implement final type", isOnTheFly, new LocalQuickFix[0], ProblemHighlightType.GENERIC_ERROR));
				}
		}
		return problems.toArray(ProblemDescriptor[]::new);
	}
}