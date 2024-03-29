package cyclic.intellij.asJava;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.lang.jvm.types.JvmArrayType;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightMethodBuilder;
import com.intellij.psi.impl.light.LightPsiClassBuilder;
import com.intellij.psi.search.GlobalSearchScope;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.common.CycParameter;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.*;
import cyclic.intellij.psi.utils.JvmClassUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AsPsiUtil{
	
	// in order to convert a CycType into a PsiClass, we may need to use that PsiClass as part of its description
	// I'd prefer a WeakIdentityHashMap, but oh well
	private static final Map<String, PsiClass> converting = new HashMap<>();
	private static String topConversion = null;
	
	// TODO: do something more like JvmCyclicClass.of, please
	
	@NotNull
	public static PsiClass asPsiClass(CycType type){
		if(topConversion == null)
			topConversion = type.fullyQualifiedName();
		else if(converting.containsKey(type.fullyQualifiedName()))
			return converting.get(type.fullyQualifiedName());
		
		var builder = new CycAsPsiClass(type);
		converting.put(type.fullyQualifiedName(), builder);
		
		for(JvmMethod method : type.declaredMethods()){
			if(method instanceof JvmCyclicMethod cycMethod){
				PsiMethod mBuilder = asPsiMethod(cycMethod.getUnderlying());
				builder.addMethod(mBuilder);
			}
		}
		
		for(JvmClass inter : type.getInterfaces()){
			PsiClass clss = inter instanceof PsiClass ? (PsiClass)inter :
			                inter instanceof CycType ? asPsiClass((CycType)inter) : null;
			if(clss != null)
				(type.kind() == CycKind.INTERFACE ? builder.getExtendsList() : builder.getImplementsList())
						.addReference(clss);
		}
		var sType = type.getSuperType();
		if(sType != null){
			PsiClass clss = sType instanceof PsiClass ? (PsiClass)sType :
			                sType instanceof CycType ? asPsiClass((CycType)sType) : null;
			if(clss != null)
				builder.getExtendsList().addReference(clss);
		}
		// TODO: fields
		
		if(topConversion != null && topConversion.equals(type.fullyQualifiedName())){
			converting.clear(); // conversion types are only valid for one conversion session
			topConversion = null;
		}
		
		return builder;
	}
	
	@NotNull
	public static PsiMethod asPsiMethod(CycMethod method){
		var mBuilder = new LightMethodBuilder(method.getManager(), CyclicLanguage.LANGUAGE, method.getName());
		mBuilder.setNavigationElement(method);
		// need to query modifiers instead of listing in the case of implicit modifiers
		// TODO: change getModifiers?
		for(JvmModifier value : JvmModifier.values())
			if(value != JvmModifier.PACKAGE_LOCAL){
				String mod = value.toString().toLowerCase();
				if(method.hasModifier(mod))
					mBuilder.addModifier(mod);
			}
		for(CycParameter parameter : method.parameters()){
			var jType = parameter.varType();
			var psiType = asPsiType(jType);
			if(psiType != null){
				if(parameter.isVarargs() && psiType instanceof PsiArrayType arr)
					psiType = arr.getComponentType(); // wrapped in PsiEllipsesType for us
				mBuilder.addParameter(parameter.varName(), psiType, parameter.isVarargs());
			}else
				mBuilder.addParameter(parameter.varName(), JvmClassUtils.name(parameter.varType()));
		}
		mBuilder.setMethodReturnType(asPsiType(method.returnType()));
		return mBuilder;
	}
	
	public static PsiType asPsiType(JvmType type){
		if(type instanceof PsiType pType)
			return pType;
		if(type instanceof JvmArrayType arrType)
			return new PsiArrayType(asPsiType(arrType.getComponentType()));
		if(type instanceof JvmReferenceType refType){
			var resolve = refType.resolve();
			if(resolve instanceof PsiClass classType)
				return new CycPsiClassReferenceType(classType);
			if(resolve instanceof JvmCyclicClass cycType)
				return new CycPsiClassReferenceType(asPsiClass(cycType.getUnderlying()));
		}
		return null; // should never occur
	}
	
	public static class CycPsiClassReferenceType extends PsiClassType.Stub{
		
		private final PsiClass underlying;
		
		// TODO: un-hardcode
		protected CycPsiClassReferenceType(PsiClass underlying){
			super(LanguageLevel.HIGHEST, PsiAnnotation.EMPTY_ARRAY);
			this.underlying = underlying;
		}
		
		public @NotNull String getPresentableText(boolean annotated){
			return underlying.getName();
		}
		
		public @NotNull String getCanonicalText(boolean annotated){
			return underlying.getQualifiedName();
		}
		
		public boolean isValid(){
			return underlying.isValid();
		}
		
		public boolean equalsToText(@NotNull @NonNls String text){
			return getCanonicalText().equals(text);
		}
		
		public @Nullable PsiClass resolve(){
			return underlying;
		}
		
		public String getClassName(){
			return underlying.getName();
		}
		
		public PsiType @NotNull [] getParameters(){
			return new PsiType[0];
		}
		
		public @NotNull ClassResolveResult resolveGenerics(){
			return new ClassResolveResult(){
				public PsiClass getElement(){
					return resolve();
				}
				
				public @NotNull PsiSubstitutor getSubstitutor(){
					return PsiSubstitutor.EMPTY;
				}
				
				public boolean isPackagePrefixPackageReference(){
					return false;
				}
				
				public boolean isAccessible(){
					return true; // TODO: check accessibility from java
				}
				
				public boolean isStaticsScopeCorrect(){
					return false;
				}
				
				public PsiElement getCurrentFileResolveScope(){
					return null;
				}
				
				public boolean isValidResult(){
					return false;
				}
			};
		}
		
		public @NotNull PsiClassType rawType(){
			return this;
		}
		
		public @NotNull GlobalSearchScope getResolveScope(){
			return GlobalSearchScope.fileScope(underlying.getContainingFile());
		}
		
		public @NotNull LanguageLevel getLanguageLevel(){
			return LanguageLevel.HIGHEST;
		}
		
		public @NotNull PsiClassType setLanguageLevel(@NotNull LanguageLevel languageLevel){
			return this;
		}
		
		public boolean equals(Object o){
			if(this == o)
				return true;
			if(!(o instanceof CycPsiClassReferenceType type))
				return false;
			if(!super.equals(o))
				return false;
			
			return Objects.equals(underlying, type.underlying);
		}
		
		public int hashCode(){
			int result = super.hashCode();
			result = 31 * result + (underlying != null ? underlying.hashCode() : 0);
			return result;
		}
	}
	
	private static class CycAsPsiClass extends LightPsiClassBuilder{
		private final SmartPsiElementPointer<CycType> pointer;
		
		public CycAsPsiClass(CycType type){
			super(type, type.name());
			this.pointer = SmartPointerManager.createPointer(type);
		}
		
		public String getQualifiedName(){
			return deref().fullyQualifiedName();
		}
		
		public @NotNull PsiElement getNavigationElement(){
			return deref();
		}
		
		public @Nullable Icon getIcon(int flags){
			return deref().getIcon(flags);
		}
		
		public PsiFile getContainingFile(){
			return isValid() ? deref().getContainingFile() : null;
		}
		
		public boolean isInterface(){
			return deref().kind() == CycKind.INTERFACE;
		}
		
		public boolean isAnnotationType(){
			return deref().kind() == CycKind.ANNOTATION;
		}
		
		public boolean isEnum(){
			return deref().kind() == CycKind.ENUM;
		}
		
		public boolean isValid(){
			return deref().isValid();
		}
		
		private CycType deref(){
			return pointer.dereference();
		}
	}
}