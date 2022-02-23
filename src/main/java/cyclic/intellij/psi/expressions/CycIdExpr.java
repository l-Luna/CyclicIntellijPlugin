package cyclic.intellij.psi.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmField;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.psi.CycExpression;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.CycIdPart;
import cyclic.intellij.psi.CycType;
import cyclic.intellij.psi.types.ClassTypeImpl;
import cyclic.intellij.psi.utils.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@SuppressWarnings("UnstableApiUsage")
public class CycIdExpr extends CycExpression implements PsiReference{
	
	public CycIdExpr(@NotNull ASTNode node){
		super(node);
	}
	
	/*
	 Can resolve to:
	 - a local variable (int x = 0; int y = x;)
	 - a type (CycExpression)
	 - a field (CycExpression.COMPOSITE_ELEMENT_WRAPPER)
	 - a package (cyclic.intellij.psi.CycExpression)
	*/
	
	public String id(){
		return PsiUtils.childOfType(this, CycIdPart.class).map(PsiElement::getText).orElse("");
	}
	
	// [JvmClass | PsiPackage | CycVariable | JvmField | String | null]
	public Object resolveTarget(){
		var on = PsiUtils.childOfType(this, CycExpression.class).orElse(null);
		String id = id();
		if(on == null){
			var scope = CycVarScope.scopeOf(this);
			if(scope.isPresent()){
				var available = scope.get().available();
				var byName = available.stream().filter(x -> x.varName().equals(id)).findFirst();
				if(byName.isPresent())
					return byName.get();
			}
			CycType inside = PsiTreeUtil.getParentOfType(this, CycType.class);
			if(inside != null){
				var field = inside.fields().stream()
						.filter(x -> x.varName().equals(id))
						.findFirst()
						.orElse(null);
				if(field != null)
					return field;
			}
			return resolveById(id, getContainingFile(), getProject());
		}else{
			Object ret = null;
			if(on instanceof CycIdExpr){
				var res = ((CycIdExpr)on).resolveTarget();
				if(res == null)
					return null;
				String narrowId = id;
				if(res instanceof String)
					narrowId = res + "." + narrowId;
				if(res instanceof PsiPackage)
					narrowId = ((PsiPackage)res).getQualifiedName() + "." + narrowId;
				ret = resolveById(narrowId, getContainingFile(), getProject());
			}
			JvmType type = on.type();
			if(!(type instanceof JvmReferenceType))
				return ret;
			var res = ((JvmReferenceType)type).resolve();
			if(!(res instanceof JvmClass))
				return ret;
			return Arrays.stream(((JvmClass)res).getFields())
					.filter(x -> x.getName().equals(id))
					.findFirst()
					.map(Object.class::cast)
					.orElse(ret);
		}
	}
	
	@NotNull
	private static Object resolveById(String id, PsiFile file, Project project){
		if(file instanceof CycFile){
			var type = ProjectTypeFinder.firstType(project, CycTypeReference.getCandidates((CycFile)file, id));
			if(type != null)
				return type;
		}
		var psiPackage = JavaPsiFacade.getInstance(project).findPackage(id);
		if(psiPackage != null)
			return psiPackage;
		return id;
	}
	
	public @Nullable JvmType type(){
		var res = resolveTarget();
		if(res instanceof JvmClass)
			return ClassTypeImpl.of((JvmClass)res);
		if(res instanceof CycVariable)
			return ((CycVariable)res).varType();
		if(res instanceof JvmField)
			return ((JvmField)res).getType();
		return null;
	}
	
	public PsiReference getReference(){
		return this;
	}
	
	public @NotNull PsiElement getElement(){
		return this;
	}
	
	public @NotNull TextRange getRangeInElement(){
		return PsiUtils.childOfType(this, CycIdPart.class).map(PsiElement::getTextRangeInParent).orElse(getTextRangeInParent());
	}
	
	public @Nullable PsiElement resolve(){
		var res = resolveTarget();
		if(res instanceof JvmClass)
			return ((JvmClass)res).getSourceElement();
		if(res instanceof PsiElement)
			return (PsiElement)res;
		return null;
	}
	
	public @NotNull @NlsSafe String getCanonicalText(){
		var res = resolveTarget();
		if(res instanceof JvmClass){
			var qName = ((JvmClass)res).getQualifiedName();
			return qName != null ? qName : "<anonymous>";
		}
		if(res instanceof PsiPackage)
			return ((PsiPackage)res).getQualifiedName();
		if(res instanceof CycVariable)
			return ((CycVariable)res).varName();
		return "<invalid reference>";
	}
	
	public PsiElement handleElementRename(@NotNull String name) throws IncorrectOperationException{
		PsiUtils.childOfType(this, CycIdPart.class).ifPresent(id -> id.replace(PsiUtils.createIdFromText(this, name)));
		return this;
	}
	
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException{
		if(element instanceof PsiNamedElement){
			PsiUtils.childOfType(this, CycIdPart.class).ifPresent(id -> id.replace(PsiUtils.createIdFromText(this, ((PsiNamedElement)element).getName())));
			return this;
		}
		throw new IncorrectOperationException("Can't bind an ID expression to something that has no name!");
	}
	
	public boolean isReferenceTo(@NotNull PsiElement element){
		var res = resolveTarget();
		if(res instanceof JvmClass)
			return ((JvmClass)res).getSourceElement() == element;
		if(res instanceof PsiElement)
			return res == element;
		return false;
	}
	
	public boolean isSoft(){
		return false;
	}
}