package cyclic.intellij.presentation.compilerLints;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.inspections.CompilerLinterInspection;
import cyclic.intellij.psi.CycFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CompilerExternalAnnotator extends ExternalAnnotator<PsiFile, List<CompileProblem>>{
	
	public @Nullable PsiFile collectInformation(@NotNull PsiFile file){
		return isCycFile(file) ? file : null;
	}
	
	public @Nullable List<CompileProblem> doAnnotate(PsiFile info){
		if(info == null)
			return null;
		
		return CompilerLinterManager.getProblems(info.getProject());
	}
	
	public void apply(@NotNull PsiFile file, List<CompileProblem> problems, @NotNull AnnotationHolder holder){
		if(problems == null)
			return;
		
		for(CompileProblem problem : problems){
			CompileProblem.ProblemSource source = problem.from;
			if(file instanceof PsiClassOwner){
				PsiClass target = null;
				for(PsiClass pc : ((PsiClassOwner)file).getClasses()){
					String name = pc.getQualifiedName();
					if(name != null && name.equals(problem.filename)){
						target = pc; break;
					}
				}
				if(target != null){
					String body = file.getText();
					var severity = problem.type.equals("ERROR") ? HighlightSeverity.ERROR : HighlightSeverity.WARNING;
					if(source != null && source.start != null && source.end != null){
						holder.newAnnotation(severity, problem.description)
								.range(TextRange.create(charInLineToOffset(source.start, body), charInLineToOffset(source.end, body) + 1))
								.create();
					}else{
						// compiler hasn't given us any location, put it on the class name
						if(target.getNavigationElement() instanceof PsiNameIdentifierOwner name){
							var identifier = name.getNameIdentifier();
							if(identifier != null)
								holder.newAnnotation(severity, problem.description)
										.range(identifier)
										.create();
						}
					}
				}
			}
		}
	}
	
	public static boolean isCycFile(PsiFile file){
		return file instanceof CycFile || file.getLanguage().isKindOf(CyclicLanguage.LANGUAGE);
	}
	
	public String getPairedBatchInspectionShortName(){
		return CompilerLinterInspection.SHORT_NAME;
	}
	
	// stupid way of doing it
	private static int charInLineToOffset(CompileProblem.ProblemLocation loc, String text){
		int numNewlines = loc.line - 1;
		int passed = 0;
		while(numNewlines > 0 && text.length() > passed){
			if(text.charAt(passed) == '\n')
				numNewlines--;
			passed++;
		}
		return passed + loc.column;
	}
}