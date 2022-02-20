package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PlatformIcons;
import cyclic.intellij.CyclicIcons;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.types.CPsiMethod;
import cyclic.intellij.psi.types.CPsiType;
import cyclic.intellij.psi.utils.CycModifiersHolder;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CycType extends CycDefinition implements CPsiType, CycModifiersHolder{
	
	public CycType(@NotNull ASTNode node){
		super(node);
	}
	
	public boolean isTopLevelType(){
		return getParent() instanceof CycFileWrapper;
	}
	
	public String getPackageName(){
		if(getContainingFile() instanceof CycFile){
			CycFile file = (CycFile)getContainingFile();
			return file.getPackage().map(CycPackageStatement::getPackageName).orElse("");
		}
		return "";
	}
	
	public @NotNull String fullyQualifiedName(){
		PsiFile file = getContainingFile();
		if(file instanceof CycFile)
			return ((CycFile)file).getPackage().map(k -> k.getPackageName() + ".").orElse("") + super.fullyQualifiedName();
		return super.fullyQualifiedName();
	}
	
	public @NotNull Kind kind(){
		var objType = getNode().findChildByType(Tokens.getRuleFor(CyclicLangParser.RULE_objectType));
		if(objType != null){
			if(objType.findChildByType(Tokens.TOK_CLASS) != null)
				return Kind.CLASS;
			if(objType.findChildByType(Tokens.TOK_INTERFACE) != null)
				return Kind.INTERFACE;
			if(objType.findChildByType(Tokens.TOK_ANNOTATION) != null || objType.findChildByType(Tokens.TOK_AT) != null)
				return Kind.ANNOTATION;
			if(objType.findChildByType(Tokens.TOK_ENUM) != null)
				return Kind.ENUM;
			if(objType.findChildByType(Tokens.TOK_RECORD) != null)
				return Kind.RECORD;
			if(objType.findChildByType(Tokens.TOK_SINGLE) != null)
				return Kind.SINGLE;
		}
		return Kind.CLASS;
	}
	
	public boolean isFinal(){
		return hasModifier("final");
	}
	
	public @NotNull List<? extends CPsiMethod> methods(){
		return PsiUtils.childrenOfType(this, CycMethod.class);
	}
	
	public @NotNull List<? extends CycVariable> fields(){
		List<CycVariable> defs = new ArrayList<>(PsiUtils.wrappedChildrenOfType(this, CycVariableDef.class));
		PsiUtils.childOfType(this, CycRecordComponents.class).ifPresent(rc -> defs.addAll(rc.components()));
		return defs;
	}
	
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException{
		// also change the file name if top level
		if(isTopLevelType())
			getContainingFile().setName(name + ".cyc");
		return super.setName(name);
	}
	
	public List<CycMember> getMembers(){
		return PsiUtils.childrenOfType(this, CycMember.class);
	}
	
	public PsiElement declaration(){
		return this;
	}
	
	public @NotNull String name(){
		return getName();
	}
	
	public @NotNull String packageName(){
		return getContainer().flatMap(CycFileWrapper::getPackage).map(CycPackageStatement::getPackageName).orElse("");
	}
	
	public @Nullable Icon getIcon(int flags){
		var objType = getNode().findChildByType(Tokens.getRuleFor(CyclicLangParser.RULE_objectType));
		if(objType != null){
			if(objType.findChildByType(Tokens.TOK_CLASS) != null)
				return PlatformIcons.CLASS_ICON;
			if(objType.findChildByType(Tokens.TOK_INTERFACE) != null)
				return PlatformIcons.INTERFACE_ICON;
			if(objType.findChildByType(Tokens.TOK_ANNOTATION) != null || objType.findChildByType(Tokens.TOK_AT) != null)
				return PlatformIcons.ANNOTATION_TYPE_ICON;
			if(objType.findChildByType(Tokens.TOK_ENUM) != null)
				return PlatformIcons.ENUM_ICON;
			if(objType.findChildByType(Tokens.TOK_RECORD) != null)
				return PlatformIcons.RECORD_ICON;
			if(objType.findChildByType(Tokens.TOK_SINGLE) != null)
				return CyclicIcons.SINGLE;
		}
		return CyclicIcons.CYCLIC_FILE;
	}
}