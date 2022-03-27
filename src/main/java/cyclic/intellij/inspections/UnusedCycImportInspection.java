package cyclic.intellij.inspections;

import com.intellij.codeInsight.daemon.impl.quickfix.DeleteElementFix;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.psi.CycClassReference;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.ast.CycImportStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnusedCycImportInspection extends LocalInspectionTool{
	
	public ProblemDescriptor @Nullable [] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly){
		Set<CycImportStatement> visitedImports = new HashSet<>();
		Set<CycImportStatement> allImports = new HashSet<>();
		new ImportsUsedElementVisitor(allImports, visitedImports).visitFile(file);
		List<ProblemDescriptor> problems = new ArrayList<>();
		for(CycImportStatement anImport : allImports){
			if(!visitedImports.contains(anImport))
				problems.add(manager.createProblemDescriptor(
						anImport,
						CyclicBundle.message("inspection.text.unusedImport"),
						new DeleteElementFix(anImport, CyclicBundle.message("inspection.fix.text.deleteImport")),
						ProblemHighlightType.LIKE_UNUSED_SYMBOL,
						isOnTheFly));
		}
		return problems.toArray(ProblemDescriptor.EMPTY_ARRAY);
	}
	
	public boolean runForWholeFile(){
		return true;
	}
	
	private static class ImportsUsedElementVisitor extends PsiRecursiveElementVisitor{
		private final Set<CycImportStatement> allImports;
		private final Set<CycImportStatement> visitedImports;
		
		public ImportsUsedElementVisitor(Set<CycImportStatement> allImports, Set<CycImportStatement> visitedImports){
			this.allImports = allImports;
			this.visitedImports = visitedImports;
		}
		
		public void visitFile(@NotNull PsiFile file){
			if(file instanceof CycFile)
				allImports.addAll(((CycFile)file).getImports());
			super.visitFile(file);
		}
		
		public void visitElement(@NotNull PsiElement element){
			var ref = element.getReference();
			if(!(element instanceof CycImportStatement) && ref instanceof CycClassReference){
				CycClassReference classRef = (CycClassReference)ref;
				if(!classRef.isQualified()){
					var type = classRef.resolveClass();
					if(type != null)
						if(followToImport(type, visitedImports) == null){
							var importStatement = followToImport(type, allImports);
							if(importStatement != null)
								visitedImports.add(importStatement);
						}
				}
			}
			super.visitElement(element);
		}
		
		@Nullable
		private CycImportStatement followToImport(JvmClass type, Set<CycImportStatement> list){
			for(CycImportStatement statement : list)
				if(statement.importsType(type))
					return statement;
			return null;
		}
	}
}