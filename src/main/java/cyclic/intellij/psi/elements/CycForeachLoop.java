package cyclic.intellij.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmArrayType;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.psi.CycDefinition;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import static cyclic.intellij.psi.utils.JvmClassUtils.getByName;

// Introduces the for-each variable into scope
public class CycForeachLoop extends CycDefinition implements CycVariable{
	
	public CycForeachLoop(@NotNull ASTNode node){
		super(node);
	}
	
	public String varName(){
		return getName();
	}
	
	public JvmType varType(){
		// TODO: once the compiler supports Iterables that aren't Objects, update to match
		return PsiUtils.childOfType(this, CycTypeRef.class)
				.map(CycTypeRef::asType)
				// for var/val
				.orElseGet(() -> {
					var baseType = PsiUtils.childOfType(this, CycExpression.class).map(CycExpression::type).orElse(null);
					return baseType instanceof JvmArrayType ? ((JvmArrayType)baseType).getComponentType() : getByName("java.lang.Object", getProject());
				});
	}
	
	public boolean hasModifier(String modifier){
		if(!modifier.equals("final"))
			return false;
		return getNode().findChildByType(Tokens.getFor(CyclicLangLexer.FINAL)) != null;
	}
	
	public @NotNull SearchScope getUseScope(){
		return new LocalSearchScope(getContainingFile());
	}
}