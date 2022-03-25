package cyclic.intellij.templates.live;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.macro.MacroUtil;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.containers.ContainerUtil;
import cyclic.intellij.completion.CycExpressionContributor;
import cyclic.intellij.psi.CycVarScope;
import cyclic.intellij.psi.CycVariable;
import cyclic.intellij.psi.utils.JvmClassUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CyclicVariableOfTypeMacro extends Macro{
	
	public @NonNls String getName(){
		return "cycVariable";
	}
	
	public @NlsSafe String getPresentableName(){
		return "cycVariable(Type)";
	}
	
	public String getDefaultValue() {
		return "a";
	}
	
	public @Nullable Result calculateResult(Expression @NotNull [] params, ExpressionContext context){
		var variables = getVariables(params, context);
		if(variables == null || variables.length == 0)
			return null;
		return new TextResult(variables[0].varName());
	}
	
	public LookupElement @Nullable [] calculateLookupItems(Expression @NotNull [] params, ExpressionContext context){
		var variables = getVariables(params, context);
		if(variables == null || variables.length == 0)
			return null;
		return ContainerUtil.map2Array(variables, LookupElement.class, CycExpressionContributor::getVariableElement);
	}
	
	public @Nullable CycVariable[] getVariables(Expression[] params, ExpressionContext context){
		if(params.length != 1)
			return null;
		
		Result result = params[0].calculateResult(context);
		if(result == null)
			return null;
		
		CycVarScope scope = CycVarScope.scopeOf(context.getPsiElementAtStartOffset()).orElse(null);
		if(scope == null)
			return null;
		
		var type = MacroUtil.resultToPsiType(result, context);
		List<CycVariable> elements = new ArrayList<>();
		for(CycVariable variable : scope.available())
			if(type == null || JvmClassUtils.isAssignableTo(variable.varType(), type))
				elements.add(variable);
		
		return elements.toArray(new CycVariable[0]);
	}
	
	public boolean isAcceptableInContext(TemplateContextType context){
		return context instanceof CyclicTemplateContextType;
	}
}
