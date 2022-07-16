package cyclic.intellij.run;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileTask;

public class CyclicCompileTask implements CompileTask{
	
	public boolean execute(CompileContext context){
		return CompilerInvoker.invoke(context.getProject(), context::addMessage);
	}
}