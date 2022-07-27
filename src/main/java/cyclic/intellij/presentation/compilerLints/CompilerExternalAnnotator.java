package cyclic.intellij.presentation.compilerLints;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiFile;
import cyclic.intellij.CyclicLanguage;
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
			if(file instanceof PsiClassOwner && Arrays.stream(((PsiClassOwner)file).getClasses())
					.map(PsiClass::getQualifiedName)
					.filter(Objects::nonNull)
					.anyMatch(y -> y.equals(problem.filename)))
				if(source.start != null && source.end != null){
					String body = file.getText();
					holder.newAnnotation(HighlightSeverity.WARNING, "[" + problem.type + "] " + problem.description)
							.range(TextRange.create(charInLineToOffset(source.start, body), charInLineToOffset(source.end, body) + 1))
							.create();
				}
		}
	}
	
	public static boolean isCycFile(PsiFile file){
		return file instanceof CycFile || file.getLanguage().isKindOf(CyclicLanguage.LANGUAGE);
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