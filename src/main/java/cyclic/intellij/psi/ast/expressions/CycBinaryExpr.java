package cyclic.intellij.psi.ast.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPrimitiveType;
import cyclic.intellij.psi.ast.CycBinaryOp;
import cyclic.intellij.psi.utils.JvmClassUtils;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

import static cyclic.intellij.psi.utils.JvmClassUtils.getByName;

public class CycBinaryExpr extends CycExpression{
	
	public CycBinaryExpr(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycBinaryOp> op(){
		return PsiUtils.childOfType(this, CycBinaryOp.class);
	}
	
	public String symbol(){
		return op().map(PsiElement::getText).orElse("");
	}
	
	public Optional<CycExpression> left(){
		return PsiUtils.childOfType(this, CycExpression.class, 0);
	}
	
	public Optional<CycExpression> right(){
		return PsiUtils.childOfType(this, CycExpression.class, 1);
	}
	
	public @Nullable JvmType type(){
		switch(symbol()){
			case "":
				return null;
			case "+":
				// consider string addition
				var lType = left().map(CycExpression::type).orElse(null);
				if(lType instanceof JvmReferenceType){
					var resolve = ((JvmReferenceType)lType).resolve();
					if(resolve instanceof JvmClass && Objects.equals(((JvmClass)resolve).getQualifiedName(), "java.lang.String"))
						return getByName("java.lang.String", getProject());
				}
				var rType = right().map(CycExpression::type).orElse(null);
				if(rType instanceof JvmReferenceType){
					var resolve = ((JvmReferenceType)rType).resolve();
					if(resolve instanceof JvmClass && Objects.equals(((JvmClass)resolve).getQualifiedName(), "java.lang.String"))
						return getByName("java.lang.String", getProject());
				}
				// otherwise fall-through
			case "-":
			case "*":
			case "/":
			case "%":
			case "&":
			case "|":
			case "^":
			case "<<":
			case ">>":
			case "<<<":
			case ">>>":
				return JvmClassUtils.highest(
						left().map(CycExpression::type).orElse(null),
						right().map(CycExpression::type).orElse(null));
			case "&&":
			case "||":
			case "==":
			case "!=":
			case ">=":
			case "<=":
			case ">":
			case "<":
				return PsiPrimitiveType.BOOLEAN;
			case "|>":
				return right().map(CycExpression::type).orElse(null);
		}
		return null;
	}
}