package cyclic.intellij.psi.utils;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.lang.jvm.types.JvmArrayType;
import com.intellij.lang.jvm.types.JvmPrimitiveType;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.search.GlobalSearchScope;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.ClassTypeImpl;
import cyclic.intellij.psi.types.JvmCyclicClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.intellij.lang.jvm.types.JvmPrimitiveTypeKind.*;

public class JvmClassUtils{
	
	@NotNull
	public static String getPackageName(@Nullable JvmClass jClass){
		if(jClass == null)
			return "";
		String name = jClass.getName(), qName = jClass.getQualifiedName();
		if(name == null || qName == null)
			return "";
		return qName.substring(0, qName.length() - 1 - name.length());
	}
	
	@Nullable
	public static JvmType getByName(String name, Project in){
		return ClassTypeImpl.of(JavaPsiFacade.getInstance(in).findClass(name, GlobalSearchScope.everythingScope(in)));
	}
	
	@Nullable
	public static JvmClass asClass(CycType type){
		return JvmCyclicClass.of(type);
	}
	
	@Nullable
	public static JvmType asType(CycType type){
		return ClassTypeImpl.of(JvmCyclicClass.of(type));
	}
	
	@NotNull
	public static String name(JvmType type){
		if(type instanceof JvmArrayType)
			return name(((JvmArrayType)type).getComponentType()) + "[]";
		if(type instanceof JvmPrimitiveType && !type.equals(PsiPrimitiveType.NULL))
			return ((JvmPrimitiveType)type).getKind().getName();
		if(type instanceof JvmReferenceType)
			return ((JvmReferenceType)type).getName();
		return "";
	}
	
	@Nullable
	public static JvmClass asClass(JvmType type){
		if(type instanceof JvmReferenceType){
			var res = ((JvmReferenceType)type).resolve();
			return res instanceof JvmClass ? (JvmClass)res : null;
		}
		return null;
	}
	
	@NotNull
	public static List<JvmMethod> getMethods(@Nullable JvmType type){
		var clss = asClass(type);
		if(clss == null)
			return List.of();
		// TODO: array #clone
		return List.of(clss.getMethods());
	}
	
	public static boolean isAssignableTo(@Nullable JvmType value, @Nullable JvmType to){
		if(value == null || to == null)
			return to == value;
		if(to instanceof JvmPrimitiveType){
			return value instanceof JvmPrimitiveType
					&& value != PsiPrimitiveType.NULL
					&& (((JvmPrimitiveType)value).getKind() == ((JvmPrimitiveType)to).getKind());
		}
		if(to instanceof JvmArrayType)
			return value instanceof JvmArrayType && (isAssignableTo(((JvmArrayType)value).getComponentType(), ((JvmArrayType)to).getComponentType()));
		if(to instanceof JvmReferenceType){
			if(value instanceof JvmPrimitiveType)
				return value == PsiPrimitiveType.NULL;
			var toClass = asClass(to);
			if(toClass != null){
				if(toClass.getQualifiedName() != null && toClass.getQualifiedName().equals("java.lang.Object"))
					return !(value instanceof JvmPrimitiveType);
				return isClassAssignableTo(asClass(value), toClass);
			}
		}
		return false;
	}
	
	public static boolean isConvertibleTo(@Nullable JvmType value, @Nullable JvmType to){
		if(value == null || to == null)
			return to == value;
		if(to instanceof JvmPrimitiveType){
			if(value instanceof JvmPrimitiveType){
				if(value == PsiPrimitiveType.NULL)
					return false;
				var k = ((JvmPrimitiveType)value).getKind();
				var tk = ((JvmPrimitiveType)to).getKind();
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
				return name != null && name.equals(((JvmPrimitiveType)to).getKind().getBoxedFqn());
			}
		}
		if(to instanceof JvmArrayType) // Integer[] != int[]
			return value instanceof JvmArrayType && (isAssignableTo(((JvmArrayType)value).getComponentType(), ((JvmArrayType)to).getComponentType()));
		if(to instanceof JvmReferenceType){
			if(value instanceof JvmPrimitiveType)
				return value == PsiPrimitiveType.NULL;
			var toClass = asClass(to);
			if(toClass != null){
				if(toClass.getQualifiedName() != null && toClass.getQualifiedName().equals("java.lang.Object"))
					return !(value instanceof JvmPrimitiveType);
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
	
	public static @Nullable JvmMethod findMethodInHierarchy(JvmClass jClass, Predicate<JvmMethod> filter, boolean strict){
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
	public static List<JvmMethod> findAllMethodsInHierarchy(JvmClass jClass, Predicate<JvmMethod> filter, boolean strict){
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
	
	public static @Nullable JvmType highest(@Nullable JvmType left, @Nullable JvmType right){
		if(left == null || right == null)
			return null;
		if(isConvertibleTo(left, right))
			return right;
		if(isConvertibleTo(right, left))
			return left;
		return null;
	}
}