package cyclic.intellij.presentation.compilerLints;

import com.intellij.codeHighlighting.*;
import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationSession;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiFile;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.psi.CycFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// TODO: see ProgressableTextEditorHighlightingPass
public class CompilerLinterPass extends TextEditorHighlightingPass{
	
	private List<CompileProblem> myProblems = null;
	private AnnotationHolderImpl holder;
	private PsiFile file;
	
	@SuppressWarnings("deprecation") // this is how ExternalToolPass does it
	protected CompilerLinterPass(@NotNull PsiFile file, @NotNull Project project, @NotNull Document document){
		super(project, document);
		this.file = file;
		holder = new AnnotationHolderImpl(new AnnotationSession(file), false);
	}
	
	public void doCollectInformation(@NotNull ProgressIndicator progress){
		// TODO: cancel using ProgressIndicator
		myProblems = CompilerLinterManager.getProblems(myProject);
	}
	
	public void doApplyInformationToEditor(){
		if(!CompilerLinterManager.isUpToDate(myProject))
			return;
		
		if(myProblems == null)
			return;
		
		holder.runAnnotatorWithContext(file, (elem_, holder_) -> {
			for(CompileProblem problem : myProblems){
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
		});
		
		UpdateHighlightersUtil.setHighlightersToEditor(myProject, myDocument, 0, file.getTextLength(), getHighlights(), getColorsScheme(), getId());
	}
	
	private @NotNull List<HighlightInfo> getHighlights(){
		List<HighlightInfo> infos = new ArrayList<>(holder.size());
		for(Annotation annotation : holder){
			infos.add(HighlightInfo.fromAnnotation(annotation));
		}
		return infos;
	}
	
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
	
	public static class Factory implements TextEditorHighlightingPassFactory{
		public @Nullable TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile file, @NotNull Editor editor){
			return isCycFile(file) ? new CompilerLinterPass(file, file.getProject(), editor.getDocument()) : null;
		}
		
		// this is stupid
		public static class Register implements TextEditorHighlightingPassFactoryRegistrar{
			public void registerHighlightingPassFactory(@NotNull TextEditorHighlightingPassRegistrar registrar, @NotNull Project project){
				registrar.registerTextEditorHighlightingPass(new Factory(), new int[]{Pass.UPDATE_ALL}, null, true, -1);
			}
		}
	}
	
	public static boolean isCycFile(PsiFile file){
		return file instanceof CycFile || file.getLanguage().isKindOf(CyclicLanguage.LANGUAGE);
	}
}