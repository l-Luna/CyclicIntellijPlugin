package cyclic.intellij.psi.utils;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.types.JvmArrayType;
import com.intellij.lang.jvm.types.JvmPrimitiveType;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.search.GlobalSearchScope;
import cyclic.intellij.psi.CycType;
import cyclic.intellij.psi.types.ClassTypeImpl;
import cyclic.intellij.psi.types.JvmCyclicClass;

import java.util.Locale;

public class JvmClassUtils{
	
	public static String getPackageName(JvmClass jClass){
		String name = jClass.getName(), qName = jClass.getQualifiedName();
		if(name == null || qName == null)
			return "";
		return qName.substring(0, qName.length() - 1 - name.length());
	}
	
	public static JvmType getByName(String name, Project in){
		return ClassTypeImpl.of(JavaPsiFacade.getInstance(in).findClass(name, GlobalSearchScope.everythingScope(in)));
	}
	
	public static JvmClass asClass(CycType type){
		return JvmCyclicClass.of(type);
	}
	
	public static JvmType asType(CycType type){
		return ClassTypeImpl.of(JvmCyclicClass.of(type));
	}
	
	public static String name(JvmType type){
		if(type instanceof JvmArrayType)
			return name(((JvmArrayType)type).getComponentType()) + "[]";
		if(type instanceof JvmPrimitiveType)
			return ((JvmPrimitiveType)type).getKind().getName();
		if(type instanceof JvmReferenceType)
			return ((JvmReferenceType)type).getName();
		return "";
	}
}