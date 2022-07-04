package cyclic.intellij.psi.types;

import com.intellij.lang.jvm.JvmMethod;
import com.intellij.psi.OriginInfoAwareElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.light.LightMethodBuilder;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.psi.CycVariable;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.common.CycParameter;
import cyclic.intellij.psi.ast.types.CycRecordComponents;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.stubs.StubCycMemberWrapper;
import cyclic.intellij.psi.stubs.StubCycMethod;
import cyclic.intellij.psi.stubs.StubTypes;
import cyclic.intellij.psi.utils.PsiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cyclic.intellij.asJava.AsPsiUtil.asPsiType;

public final class ImplicitMembers{
	
	private static final String NON_RENAMABLE_ORIGIN = "Cyclic: non-renamable implicit method";
	
	public static List<JvmMethod> implicitMethodsOf(CycType type){
		List<JvmMethod> implicits = new ArrayList<>();
		if(type.kind() == CycKind.RECORD){
			var explicitMethods = explicitMethods(type);
			for(CycParameter comp : recordComponents(type))
				if(explicitMethods.stream().noneMatch(m -> m.getParameters().length == 0 && Objects.equals(m.getName(), comp.varName())))
					implicits.add(recordAccessorMethod(comp));
		}else if(type.kind() == CycKind.ENUM){
			implicits.add(enumValuesMethod(type));
			implicits.add(enumValueOfMethod(type));
			implicits.add(enumEntriesMethod(type));
		}
		return implicits;
	}
	
	public static boolean isNotFixed(Object d){
		return !(d instanceof OriginInfoAwareElement && NON_RENAMABLE_ORIGIN.equals(((OriginInfoAwareElement)d).getOriginInfo()));
	}
	
	private static PsiMethod recordAccessorMethod(CycVariable backing){
		var builder = new LightMethodBuilder(backing.getManager(), CyclicLanguage.LANGUAGE, backing.varName());
		builder.setNavigationElement(backing);
		builder.addModifier("public");
		builder.addModifier("final");
		builder.setMethodReturnType(asPsiType(backing.varType()));
		return builder;
	}
	
	private static PsiMethod enumValuesMethod(CycType of){
		var builder = new LightMethodBuilder(of.getManager(), CyclicLanguage.LANGUAGE, "values");
		builder.setNavigationElement(of);
		builder.addModifier("public");
		builder.addModifier("static");
		builder.setMethodReturnType(asPsiType(ArrayTypeImpl.of(ClassTypeImpl.of(of))));
		builder.setOriginInfo(NON_RENAMABLE_ORIGIN);
		return builder;
	}
	
	private static PsiMethod enumValueOfMethod(CycType of){
		var builder = new LightMethodBuilder(of.getManager(), CyclicLanguage.LANGUAGE, "valueOf");
		builder.setNavigationElement(of);
		builder.addModifier("public");
		builder.addModifier("static");
		builder.addParameter("name", "java.lang.String");
		builder.setMethodReturnType(asPsiType(ClassTypeImpl.of(of)));
		builder.setOriginInfo(NON_RENAMABLE_ORIGIN);
		return builder;
	}
	
	private static PsiMethod enumEntriesMethod(CycType of){
		var builder = new LightMethodBuilder(of.getManager(), CyclicLanguage.LANGUAGE, "entries");
		builder.setNavigationElement(of);
		builder.addModifier("public");
		builder.addModifier("static");
		builder.setMethodReturnType("java.util.List");
		builder.setOriginInfo(NON_RENAMABLE_ORIGIN);
		return builder;
	}
	
	private static List<JvmMethod> explicitMethods(CycType type){
		if(type.getStub() != null)
			return explicitMethodsByStub(type);
		else
			return explicitMethodsByAst(type);
	}
	
	private static List<JvmMethod> explicitMethodsByAst(CycType type){
		return PsiUtils.streamWrappedChildrenOfType(type, CycMethod.class).map(JvmCyclicMethod::of).collect(Collectors.toList());
	}
	
	@SuppressWarnings({"ConstantConditions", "unchecked"})
	private static List<JvmMethod> explicitMethodsByStub(CycType type){
		return type.getStub().getChildrenStubs().stream()
				.filter(StubCycMemberWrapper.class::isInstance)
				.flatMap(x -> (Stream<StubElement<?>>)x.getChildrenStubs().stream())
				.filter(StubCycMethod.class::isInstance)
				.map(StubCycMethod.class::cast)
				.map(StubElement::getPsi)
				.map(JvmCyclicMethod::of)
				.collect(Collectors.toList());
	}
	
	private static List<CycParameter> recordComponents(CycType type){
		if(type.getStub() != null){
			var componentList = type.getStub().findChildStubByType(StubTypes.CYC_RECORD_COMPONENTS);
			return componentList == null
					? List.of()
					: componentList.components().stream().map(StubElement::getPsi).collect(Collectors.toList());
		}else
			return PsiUtils.childOfType(type, CycRecordComponents.class)
				.map(CycRecordComponents::components)
				.orElse(List.of());
	}
}