package cyclic.intellij.psi.ast.common;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.lang.jvm.types.JvmArrayType;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.ast.CycIdPart;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.expressions.CycExpression;
import cyclic.intellij.psi.ast.expressions.CycIdExpr;
import cyclic.intellij.psi.ast.expressions.CycParenthesisedExpr;
import cyclic.intellij.psi.ast.statements.CycStatement;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.CycKind;
import cyclic.intellij.psi.types.ImplicitMembers;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.JvmClassUtils;
import cyclic.intellij.psi.utils.MethodUtils;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CycCall extends CycAstElement implements PsiReference, CycStatement{
	
	public CycCall(@NotNull ASTNode node){
		super(node);
	}
	
	// FIXME: update to match compiler logic
	
	@Nullable
	public JvmMethod resolveMethod(){
		class Target{
			final JvmMethod ref;
			final int reach;
			
			Target(JvmMethod ref, int reach){
				this.ref = ref;
				this.reach = reach;
			}
		}
		// we don't necessarily have all arguments as children:
		//  our receiver is one level up
		//  pass expressions can be any number of levels up, so long as there's only passes or parenthesis
		// TODO: super calls, constructors
		CycExpression on = getOn();
		List<CycExpression> args = MethodUtils.getRealArgs(this);
		String name = getCanonicalText();
		
		List<Target> targets = new ArrayList<>();
		List<JvmMethod> candidates;
		while(on instanceof CycParenthesisedExpr)
			on = PsiUtils.childOfType(on, CycExpression.class).orElse(null);
		if(on != null){
			boolean staticOnly = false;
			if(on instanceof CycIdExpr){
				var target = ((CycIdExpr)on).resolveTarget();
				if(target instanceof JvmClass)
					if(!(target instanceof JvmCyclicClass && ((JvmCyclicClass)target).getUnderlying().kind() == CycKind.SINGLE))
						staticOnly = true;
			}
			boolean temp = staticOnly;
			candidates = JvmClassUtils.findAllMethodsInHierarchy(JvmClassUtils.asClass(on.type()),
					m -> !temp || m.hasModifier(JvmModifier.STATIC),
					false);
		}else
			candidates = getStandaloneCandidates(this);
		
		candidates:
		for(JvmMethod x : candidates){
			// TODO: visibility
			if(x.getName().equals(name)){
				List<JvmType> parameters = Arrays.stream(x.getParameters()).map(JvmParameter::getType).collect(Collectors.toList());
				int reach;
				varargs:
				if(x.isVarArgs()){
					if(args.size() < parameters.size() - 1)
						break varargs;
					reach = 2;
					for(int i = 0; i < args.size(); i++){
						CycExpression arg = args.get(i);
						JvmType checking;
						if(i + 1 < parameters.size())
							checking = parameters.get(i);
						else{
							checking = parameters.get(parameters.size() - 1);
							if(checking instanceof JvmArrayType){
								JvmArrayType arr = (JvmArrayType)checking;
								checking = arr.getComponentType();
							}else
								break varargs; // invalid varargs method
						}
						if(arg.isAssignableTo(checking))
							continue;
						if(arg.isConvertibleTo(checking)){
							reach = 3;
							continue;
						}
						break varargs;
					}
					targets.add(new Target(x, reach));
				}
				if(x.getParameters().length != args.size())
					continue;
				reach = 0;
				for(int i = 0; i < parameters.size(); i++){
					JvmType pTarget = parameters.get(i);
					// erase generics
					// I'd put a to-do for generics, but this'll be rewritten anyways
					pTarget = JvmClassUtils.eraseGenerics(pTarget, getProject());
					CycExpression arg = args.get(i);
					if(arg.type() != null && arg.isAssignableTo(pTarget))
						continue;
					if(arg.isConvertibleTo(pTarget)){
						reach = 1;
						continue;
					}
					continue candidates;
				}
				targets.add(new Target(x, reach));
			}
		}
		return targets
				.stream()
				.min(Comparator.comparingInt(x -> x.reach))
				.map(x -> x.ref)
				.orElse(null);
	}
	
	@NotNull
	public static List<JvmMethod> getStandaloneCandidates(PsiElement at){
		List<JvmMethod> candidates;
		var inMethod = PsiTreeUtil.getParentOfType(at, CycMethod.class);
		var inType = PsiTreeUtil.getParentOfType(at, CycType.class);
		if(inType != null)
			candidates = (inMethod != null && inMethod.isStatic())
					? inType.declaredMethods().stream().filter(x -> x.hasModifier(JvmModifier.STATIC)).collect(Collectors.toList())
					: new ArrayList<>(inType.declaredMethods());
		else
			candidates = Collections.emptyList();
		return candidates;
	}
	
	@Nullable
	public CycExpression getOn(){
		return PsiUtils.childOfType(getParent(), CycExpression.class).orElse(null);
	}
	
	public PsiReference getReference(){
		return getMethodName() != null ? this : null;
	}
	
	public PsiElement getMethodName(){
		return PsiUtils.childOfType(this, CycIdPart.class).orElse(null);
	}
	
	public @NotNull PsiElement getElement(){
		return this;
	}
	
	public @NotNull TextRange getRangeInElement(){
		return getMethodName().getTextRangeInParent();
	}
	
	public @Nullable PsiElement resolve(){
		var method = resolveMethod();
		PsiElement source = method != null ? method.getSourceElement() : null;
		return source != null ? (ImplicitMembers.isNotFixed(source) ? source.getNavigationElement() : source) : null;
	}
	
	public @NotNull @NlsSafe String getCanonicalText(){
		return getMethodName().getText();
	}
	
	public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException{
		getMethodName().replace(PsiUtils.createIdPartFromText(this, newElementName));
		return this;
	}
	
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException{
		if(element instanceof PsiNamedElement)
			getMethodName().replace(PsiUtils.createIdPartFromText(this, ((PsiNamedElement)element).getName()));
		else
			throw new IncorrectOperationException("Can't bind a method call to something that has no name!");
		return this;
	}
	
	public boolean isReferenceTo(@NotNull PsiElement element){
		return element == resolve();
	}
	
	public boolean isSoft(){
		return false;
	}
}