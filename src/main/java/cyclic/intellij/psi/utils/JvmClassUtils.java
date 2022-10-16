package cyclic.intellij.psi.utils;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.lang.jvm.types.JvmArrayType;
import com.intellij.lang.jvm.types.JvmPrimitiveType;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.GlobalSearchScope;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.ClassTypeImpl;
import cyclic.intellij.psi.types.JvmCyclicClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.intellij.lang.jvm.types.JvmPrimitiveTypeKind.*;

public class JvmClassUtils{
	
	@NotNull
	public static String getPackageName(@Nullable JvmClass jClass){
		if(jClass == null)
			return "";
		String name = jClass.getName(), qName = jClass.getQualifiedName();
		if(name == null || qName == null)
			return "";
		return qName.substring(0, Math.max(0, qName.length() - 1 - name.length()));
	}
	
	@Nullable
	public static JvmType typeByName(@NotNull String name, @NotNull Project in){
		return ClassTypeImpl.of(classByName(name, in));
	}
	
	@Nullable
	public static JvmClass classByName(@NotNull String name, @NotNull Project in){
		return JavaPsiFacade.getInstance(in).findClass(name, GlobalSearchScope.everythingScope(in));
	}
	
	@Nullable
	public static JvmType asType(CycType type){
		return ClassTypeImpl.of(JvmCyclicClass.of(type));
	}
	
	@NotNull
	public static String name(JvmType type){
		if(type == null)
			return "<nothing>";
		if(type instanceof JvmArrayType arrType)
			return name(arrType.getComponentType()) + "[]";
		if(Objects.equals(type, PsiPrimitiveType.NULL))
			return "null";
		if(type instanceof JvmPrimitiveType primType)
			return primType.getKind().getName();
		if(type instanceof JvmReferenceType refType)
			return refType.getName();
		return "";
	}
	
	@Nullable
	public static JvmClass asClass(@Nullable JvmType type){
		if(type instanceof JvmReferenceType refType){
			var res = refType.resolve();
			return res instanceof JvmClass ? (JvmClass)res : null;
		}
		return null;
	}
	
	public static boolean isAssignableTo(@Nullable JvmType value, @Nullable JvmType to){
		if(value == null || to == null)
			return to == value;
		if(to instanceof JvmPrimitiveType toPrim){
			return value instanceof JvmPrimitiveType fromPrim
					&& value != PsiPrimitiveType.NULL
					&& to != PsiPrimitiveType.NULL
					&& (fromPrim.getKind() == toPrim.getKind());
		}
		if(to instanceof JvmArrayType toArr)
			return value instanceof JvmArrayType fromArr && (isAssignableTo(fromArr.getComponentType(), toArr.getComponentType()));
		if(to instanceof JvmReferenceType){
			if(value instanceof JvmPrimitiveType)
				return value == PsiPrimitiveType.NULL;
			var toClass = asClass(to);
			if(toClass != null){
				if(toClass.getQualifiedName() != null && toClass.getQualifiedName().equals("java.lang.Object"))
					return true;
				return isClassAssignableTo(asClass(value), toClass);
			}
		}
		return false;
	}
	
	public static boolean isConvertibleTo(@Nullable JvmType value, @Nullable JvmType to){
		if(value == null || to == null)
			return to == value;
		if(to instanceof JvmPrimitiveType toPrim){
			if(value instanceof JvmPrimitiveType fromPrim){
				if(value == PsiPrimitiveType.NULL || to == PsiPrimitiveType.NULL)
					return false;
				var k = fromPrim.getKind();
				var tk = toPrim.getKind();
				// why isn't this an enum :p
				if(tk == BOOLEAN)
					return k == BOOLEAN;
				if(tk == SHORT || tk == CHAR)
					return k == BYTE || k == SHORT || k == CHAR;
				if(tk == INT)
					return k == BYTE || k == SHORT || k == CHAR || k == INT;
				if(tk == LONG)
					return k == BYTE || k == SHORT || k == CHAR || k == INT || k == LONG;
				if(tk == FLOAT)
					return k == BYTE || k == SHORT || k == CHAR || k == INT || k == LONG || k == FLOAT;
				if(tk == DOUBLE)
					return k == BYTE || k == SHORT || k == CHAR || k == INT || k == LONG || k == FLOAT || k == DOUBLE;
				return false;
			}
			var c = asClass(value);
			if(c != null){
				var name = c.getQualifiedName();
				return name != null && name.equals(toPrim.getKind().getBoxedFqn());
			}
		}
		if(to instanceof JvmArrayType toArr) // Integer[] != int[]
			return value instanceof JvmArrayType fromArr && (isAssignableTo(fromArr.getComponentType(), toArr.getComponentType()));
		if(to instanceof JvmReferenceType){
			if(value instanceof JvmPrimitiveType)
				return value == PsiPrimitiveType.NULL;
			var toClass = asClass(to);
			if(toClass != null){
				if(toClass.getQualifiedName() != null && toClass.getQualifiedName().equals("java.lang.Object"))
					return true;
				return isClassAssignableTo(asClass(value), toClass);
			}
		}
		return false;
	}
	
	public static boolean isClassAssignableTo(JvmClass value, JvmClass to){
		if(value != null){
			var qualName = value.getQualifiedName();
			if(qualName != null && qualName.equals(to.getQualifiedName()))
				return true;
			if(isClassAssignableTo(asClass(value.getSuperClassType()), to))
				return true;
			for(JvmReferenceType type : value.getInterfaceTypes())
				if(isClassAssignableTo(asClass(type), to))
					return true;
		}
		return false;
	}
	
	public static @Nullable JvmMethod findMethodInHierarchy(@Nullable JvmClass jClass, Predicate<JvmMethod> filter, boolean strict){
		if(jClass == null)
			return null;
		if(!strict)
			for(JvmMethod method : jClass.getMethods())
				if(filter.test(method))
					return method;
		if(jClass.getSuperClassType() != null){
			var ret = findMethodInHierarchy(asClass(jClass.getSuperClassType()), filter, false);
			if(ret != null)
				return ret;
		}
		for(JvmReferenceType type : jClass.getInterfaceTypes())
			if(type != null){
				var ret = findMethodInHierarchy(asClass(type), filter, false);
				if(ret != null)
					return ret;
			}
		return null;
	}
	
	@NotNull
	public static List<JvmMethod> findAllMethodsInHierarchy(@Nullable JvmClass jClass, Predicate<JvmMethod> filter, boolean strict){
		if(jClass == null)
			return List.of();
		var found = new ArrayList<JvmMethod>();
		if(!strict)
			for(JvmMethod method : jClass.getMethods())
				if(filter.test(method))
					found.add(method);
		if(jClass.getSuperClassType() != null)
			found.addAll(findAllMethodsInHierarchy(asClass(jClass.getSuperClassType()), filter, false));
		for(JvmReferenceType type : jClass.getInterfaceTypes())
			if(type != null)
				found.addAll(findAllMethodsInHierarchy(asClass(type), filter, false));
		return found;
	}
	
	public static List<JvmMethod> findUnimplementedMethodsFrom(@Nullable JvmClass jClass, boolean stopAtFirst, boolean onlyAbstract){
		if(jClass == null)
			return List.of();
		PsiElement source = jClass.getSourceElement();
		assert source != null;
		// find all methods to be implemented
		var abstracts = findAllMethodsInHierarchy(jClass,
				onlyAbstract
						? x -> x.hasModifier(JvmModifier.ABSTRACT) && !x.hasModifier(JvmModifier.STATIC) && x.getReturnType() != null
						: x -> !x.hasModifier(JvmModifier.FINAL) && !x.hasModifier(JvmModifier.STATIC) && x.getReturnType() != null,
				true);
		// check which ones have been implemented
		List<JvmMethod> unimplemented = new ArrayList<>();
		for(JvmMethod toImplement : abstracts)
			if(findMethodInHierarchy(jClass, x -> overrides(x, toImplement, source.getProject()) && !x.hasModifier(JvmModifier.ABSTRACT), false) == null){
				unimplemented.add(toImplement);
				if(stopAtFirst)
					break;
			}
		return unimplemented;
	}
	
	public static @Nullable JvmType highest(@Nullable JvmType left, @Nullable JvmType right){
		if(left == null || right == null)
			return null;
		if(isConvertibleTo(left, right))
			return right;
		if(isConvertibleTo(right, left))
			return left;
		return null;
	}
	
	public static boolean overrides(JvmMethod subMethod, JvmMethod superMethod, Project project){
		if(subMethod == superMethod)
			return false;
		if(subMethod == null || superMethod == null)
			return false;
		if(subMethod.hasModifier(JvmModifier.STATIC) || superMethod.hasModifier(JvmModifier.STATIC))
			return false;
		if(subMethod.hasModifier(JvmModifier.PRIVATE) || superMethod.hasModifier(JvmModifier.PRIVATE))
			return false;
		if(!Objects.equals(subMethod.getName(), superMethod.getName()))
			return false;
		JvmParameter[] subParams = subMethod.getParameters();
		JvmParameter[] superParams = superMethod.getParameters();
		if(subParams.length != superParams.length)
			return false;
		for(int i = 0; i < subParams.length; i++)
			// TODO: is this correct compiler-side?
			if(!isAssignableTo(eraseGenerics(subParams[i].getType(), project), eraseGenerics(superParams[i].getType(), project)))
				return false;
		return isAssignableTo(eraseGenerics(subMethod.getReturnType(), project), eraseGenerics(superMethod.getReturnType(), project));
	}
	
	public static String summary(@NotNull JvmMethod method){
		StringBuilder builder = new StringBuilder();
		var ret = method.getReturnType();
		builder.append(ret == null ? "new" : name(ret));
		builder.append(" ");
		builder.append(method.getName());
		builder.append(Arrays.stream(method.getParameters())
				.map(JvmParameter::getType)
				.map(JvmClassUtils::name)
				.collect(Collectors.joining(", ", "(", ")")));
		return builder.toString();
	}
	
	@Nullable
	public static JvmType eraseGenerics(@Nullable JvmType type, @NotNull Project project){
		if(type instanceof PsiClassReferenceType classType){
			var res = classType.resolve();
			if(res instanceof PsiTypeParameter){
				var bounds = res.getExtendsList();
				type = bounds != null && bounds.getReferencedTypes().length > 0
						? bounds.getReferencedTypes()[0]
						: typeByName(CommonClassNames.JAVA_LANG_OBJECT, project);
			}
		}
		return type;
	}
	
	public static boolean isMainMethod(@NotNull JvmMethod method){
		if(method.getName().equals("main"))
			if(method.getParameters().length == 1 && method.hasModifier(JvmModifier.PUBLIC) && method.hasModifier(JvmModifier.STATIC)){
				if(PsiType.VOID.equals(method.getReturnType()))
					if(method.getParameters()[0].getType() instanceof JvmArrayType array)
						if(array.getComponentType() instanceof JvmReferenceType){
							JvmClass first = asClass(array.getComponentType());
							return first != null
									&& first.getQualifiedName() != null
									&& first.getQualifiedName().equals(CommonClassNames.JAVA_LANG_STRING);
						}
			}
		return false;
	}
	
	public static boolean hasMainMethod(@NotNull JvmClass clazz){
		return Arrays.stream(clazz.getMethods()).anyMatch(JvmClassUtils::isMainMethod);
	}
}