package cyclic.intellij.presentation.compilerLints;

public class CompileProblem{
	
	// Mirrors compiler's Problem
	
	public String description, filename, type;
	public ProblemSource from;
	
	public static class ProblemSource{
		public ProblemLocation start, end;
		public String snippet, owner /*TODO: remove*/;
	}
	
	public static class ProblemLocation{
		public int column;
		public int line;
	}
}