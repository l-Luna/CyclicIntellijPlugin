package cyclic.intellij.psi;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.util.JvmMainMethodUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.LayeredIcon;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PlatformIcons;
import cyclic.intellij.CyclicIcons;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.types.CycKind;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.CycModifiersHolder;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CycType extends CycDefinition implements CycModifiersHolder{
	
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
	
	public @NotNull CycKind kind(){
		var objType = getNode().findChildByType(Tokens.getRuleFor(CyclicLangParser.RULE_objectType));
		if(objType != null){
			if(objType.findChildByType(Tokens.TOK_CLASS) != null)
				return CycKind.CLASS;
			if(objType.findChildByType(Tokens.TOK_INTERFACE) != null)
				return CycKind.INTERFACE;
			if(objType.findChildByType(Tokens.TOK_ANNOTATION) != null || objType.findChildByType(Tokens.TOK_AT) != null)
				return CycKind.ANNOTATION;
			if(objType.findChildByType(Tokens.TOK_ENUM) != null)
				return CycKind.ENUM;
			if(objType.findChildByType(Tokens.TOK_RECORD) != null)
				return CycKind.RECORD;
			if(objType.findChildByType(Tokens.TOK_SINGLE) != null)
				return CycKind.SINGLE;
		}
		return CycKind.CLASS;
	}
	
	public boolean isFinal(){
		return hasModifier("final");
	}
	
	public @NotNull List<? extends CycMethod> methods(){
		return PsiUtils.wrappedChildrenOfType(this, CycMethod.class);
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
	
	public @NotNull String name(){
		return getName();
	}
	
	public @Nullable Icon getIcon(int flags){
		var result = CyclicIcons.CYCLIC_FILE;
		var objType = getNode().findChildByType(Tokens.getRuleFor(CyclicLangParser.RULE_objectType));
		if(objType != null){
			if(objType.findChildByType(Tokens.TOK_CLASS) != null)
				result = PlatformIcons.CLASS_ICON;
			else if(objType.findChildByType(Tokens.TOK_INTERFACE) != null)
				result = PlatformIcons.INTERFACE_ICON;
			else if(objType.findChildByType(Tokens.TOK_ANNOTATION) != null || objType.findChildByType(Tokens.TOK_AT) != null)
				result = PlatformIcons.ANNOTATION_TYPE_ICON;
			else if(objType.findChildByType(Tokens.TOK_ENUM) != null)
				result = PlatformIcons.ENUM_ICON;
			else if(objType.findChildByType(Tokens.TOK_RECORD) != null)
				result = PlatformIcons.RECORD_ICON;
			else if(objType.findChildByType(Tokens.TOK_SINGLE) != null)
				result = AllIcons.Nodes.Static;
		}
		if(JvmMainMethodUtil.hasMainMethodInHierarchy(JvmCyclicClass.of(this)))
			result = new LayeredIcon(result, AllIcons.Nodes.RunnableMark);
		return new LayeredIcon(result, CyclicIcons.CYCLIC_DECORATION);
	}
	
	@Nullable
	public JvmClass getSuperType(){
		if(kind() == CycKind.INTERFACE)
			return null;
		var exts = PsiUtils.childOfType(this, CycExtendsClause.class);
		return exts.flatMap(clause -> PsiUtils.childOfType(clause, CycTypeRef.class).map(CycTypeRef::asClass)).orElse(null);
	}
	
	@NotNull
	public List<JvmClass> getInterfaces(){
		Optional<? extends CycElement> list;
		if(kind() == CycKind.INTERFACE)
			list = PsiUtils.childOfType(this, CycExtendsClause.class);
		else
			list = PsiUtils.childOfType(this, CycImplementsClause.class);
		return list.map(x -> PsiUtils.childrenOfType(x, CycTypeRef.class)
				.stream().map(CycTypeRef::asClass).collect(Collectors.toList())).orElse(List.of());
	}
}