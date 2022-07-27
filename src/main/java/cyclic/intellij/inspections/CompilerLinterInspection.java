package cyclic.intellij.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ex.ExternalAnnotatorBatchInspection;

public class CompilerLinterInspection extends LocalInspectionTool implements ExternalAnnotatorBatchInspection{
	
	public static final String SHORT_NAME = "CyclicCompilerLinter";
}