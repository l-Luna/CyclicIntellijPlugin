package cyclic.intellij.psi.ast.types;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.lang.jvm.util.JvmMainMethodUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.ui.LayeredIcon;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ui.EDT;
import cyclic.intellij.CyclicIcons;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.asJvm.AsPsiUtil;
import cyclic.intellij.psi.CycDefinitionStubElement;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.ast.*;
import cyclic.intellij.psi.stubs.*;
import cyclic.intellij.psi.types.CycKind;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.types.JvmCyclicMethod;
import cyclic.intellij.psi.utils.CycModifiersHolder;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.ProjectTypeFinder;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CycType extends CycDefinitionStubElement<CycType, StubCycType> implements CycModifiersHolder{
	
	public CycType(@NotNull ASTNode node){
		super(node);
	}
	
	public CycType(@NotNull StubCycType stub){
		super(stub, StubTypes.CYC_TYPE);
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
		var stub = getStub();
		if(stub != null)
			return stub.fullyQualifiedName();
		
		PsiFile file = getContainingFile();
		if(file instanceof CycFile)
			return ((CycFile)file).getPackage().map(k -> k.getPackageName() + ".").orElse("") + super.fullyQualifiedName();
		return super.fullyQualifiedName();
	}
	
	public @NotNull CycKind kind(){
		var stub = getStub();
		if(stub != null)
			return stub.kind();
		
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
	
	@SuppressWarnings("unchecked")
	public @NotNull List<? extends JvmMethod> declaredMethods(){
		var stub = getStub();
		if(stub != null){
			ArrayList<JvmMethod> methods = stub.getChildrenStubs().stream()
					.filter(StubCycMember.class::isInstance)
					.flatMap(x -> (Stream<StubElement<?>>)x.getChildrenStubs().stream())
					.filter(StubCycMethod.class::isInstance)
					.map(StubCycMethod.class::cast)
					.map(StubElement::getPsi)
					.map(JvmCyclicMethod::of)
					.collect(Collectors.toCollection(ArrayList::new));
			var recComponents = stub.findChildStubByType(StubTypes.CYC_RECORD_COMPONENTS);
			if(recComponents != null){
				var comps = recComponents.components();
				for(StubCycParameter comp : comps)
					if(methods.stream().noneMatch(m -> m.getParameters().length == 0 && Objects.equals(m.getName(), comp.name())))
						methods.add(AsPsiUtil.recordAccessorMethod(comp.getPsi()));
			}
			return methods;
		}
		
		List<JvmMethod> methods
				= PsiUtils.wrappedChildrenOfType(this, CycMethod.class).stream().map(JvmCyclicMethod::of).collect(Collectors.toList());
		List<CycParameter> components = PsiUtils.childOfType(this, CycRecordComponents.class)
				.map(CycRecordComponents::components)
				.orElse(List.of());
		for(CycParameter comp : components)
			if(methods.stream().noneMatch(m -> m.getParameters().length == 0 && Objects.equals(m.getName(), comp.varName())))
				methods.add(AsPsiUtil.recordAccessorMethod(comp));
		return methods;
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
		if(!DumbService.isDumb(getProject())
				&& !EDT.isCurrentThreadEdt() // TODO: can this can be refactored to not require slow operations?
				&& JvmMainMethodUtil.hasMainMethodInHierarchy(JvmCyclicClass.of(this)))
			result = new LayeredIcon(result, AllIcons.Nodes.RunnableMark);
		return new LayeredIcon(result, CyclicIcons.CYCLIC_DECORATION);
	}
	
	@Nullable
	public JvmClass getSuperType(){
		if(kind() == CycKind.INTERFACE)
			return null;
		
		var stub = getStub();
		if(stub != null){
			var extList = stub.extendsList();
			if(extList != null){
				var names = extList.elementFqNames();
				if(names.size() > 0)
					return ProjectTypeFinder.getByName(getProject(), names.get(0), this);
				else
					return null;
			}
		}
		
		var exts = PsiUtils.childOfType(this, CycExtendsClause.class);
		return exts.flatMap(CycClassList::first).orElse(null);
	}
	
	@NotNull
	public List<JvmClass> getInterfaces(){
		var stub = getStub();
		if(stub != null){
			var cl = (kind() == CycKind.INTERFACE) ? stub.extendsList() : stub.implementsList();
			if(cl != null){
				var names = cl.elementFqNames();
				return names.stream()
						.map(x -> ProjectTypeFinder.getByName(getProject(), x, this))
						.collect(Collectors.toList());
			}
		}
		
		Optional<? extends CycClassList<?>> list;
		if(kind() == CycKind.INTERFACE)
			list = PsiUtils.childOfType(this, CycExtendsClause.class);
		else
			list = PsiUtils.childOfType(this, CycImplementsClause.class);
		return list.map(CycClassList::elements).orElse(List.of());
	}
	
	public IStubElementType<StubCycType, CycType> getElementType(){
		return StubTypes.CYC_TYPE;
	}
}