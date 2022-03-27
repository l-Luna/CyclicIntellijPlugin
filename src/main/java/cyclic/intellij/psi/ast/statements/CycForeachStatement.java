package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmClassKind;
import com.intellij.lang.jvm.types.JvmArrayType;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.psi.CycDefinitionAstElement;
import cyclic.intellij.psi.CycVariable;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.ast.CycTypeRef;
import cyclic.intellij.psi.ast.CycTypeRefOrInferred;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.ast.expressions.CycIdExpr;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static cyclic.intellij.psi.utils.JvmClassUtils.typeByName;

// Introduces the for-each variable into scope
public class CycForeachStatement extends CycDefinitionAstElement implements CycVariable, CycStatement{
	
	public CycForeachStatement(@NotNull ASTNode node){
		super(node);
	}
	
	public String varName(){
		return getName();
	}
	
	public JvmType varType(){
		// TODO: once the compiler supports Iterables that aren't Objects, update to match
		return PsiUtils.childOfType(this, CycTypeRefOrInferred.class)
				.flatMap(CycTypeRefOrInferred::ref)
				.map(CycTypeRef::asType)
				// for var/val
				.orElseGet(() -> {
					Optional<CycExpression> expression = PsiUtils.childOfType(this, CycExpression.class);
					if(expression.isPresent()){
						CycExpression expr = expression.get();
						if(expr instanceof CycIdExpr){
							var target = ((CycIdExpr)expr).resolveTarget();
							if(target instanceof JvmClass && ((JvmClass)target).getClassKind() == JvmClassKind.ENUM)
								return expr.type();
						}
						var baseType = expr.type();
						if(baseType instanceof JvmArrayType)
							return ((JvmArrayType)baseType).getComponentType();
					}
					return typeByName("java.lang.Object", getProject());
				});
	}
	
	public boolean hasModifier(String modifier){
		if(!modifier.equals("final"))
			return false;
		return getNode().findChildByType(Tokens.getFor(CyclicLangLexer.FINAL)) != null;
	}
	
	public boolean isLocal(){
		return true;
	}
	
	public @NotNull SearchScope getUseScope(){
		return new LocalSearchScope(getContainingFile());
	}
	
	public Optional<CycExpression> iterator(){
		return PsiUtils.childOfType(this, CycExpression.class);
	}
	
	public Optional<CycStatement> body(){
		return PsiUtils.childOfType(this, CycStatement.class);
	}
}