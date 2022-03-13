package cyclic.intellij.asJvm;

import com.intellij.lang.jvm.JvmClass;
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
import cyclic.intellij.psi.ast.CycParameter;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.CycKind;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.JvmClassUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class AsPsiUtil{
	
	@NotNull
	public static PsiClass asPsiClass(CycType type){
		var builder = new CycAsPsiClass(type);
		
		for(CycMethod method : type.methods()){
			PsiMethod mBuilder = asPsiMethod(method);
			builder.addMethod(mBuilder);
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
		return builder;
	}
	
	@NotNull
	public static PsiMethod asPsiMethod(CycMethod method){
		var mBuilder = new LightMethodBuilder(method.getManager(), CyclicLanguage.LANGUAGE, method.getName());
		mBuilder.setNavigationElement(method);
		for(String modifier : method.getModifiers())
			mBuilder.addModifier(modifier);
		for(CycParameter parameter : method.parameters()){
			var jType = parameter.varType();
			var psiType = asPsiType(jType);
			if(psiType != null){
				if(parameter.isVarargs() && psiType instanceof PsiArrayType)
					psiType = ((PsiArrayType)psiType).getComponentType(); // wrapped in PsiEllipsesType for us
				mBuilder.addParameter(parameter.varName(), psiType, parameter.isVarargs());
			}else
				mBuilder.addParameter(parameter.varName(), JvmClassUtils.name(parameter.varType()));
		}
		mBuilder.setMethodReturnType(asPsiType(method.returnType()));
		return mBuilder;
	}
	
	public static PsiType asPsiType(JvmType type){
		if(type instanceof PsiType)
			return (PsiType)type;
		if(type instanceof JvmArrayType)
			return new PsiArrayType(asPsiType(((JvmArrayType)type).getComponentType()));
		if(type instanceof JvmReferenceType){
			var resolve = ((JvmReferenceType)type).resolve();
			if(resolve instanceof PsiClass)
				return new CycPsiClassReferenceType((PsiClass)resolve);
			if(resolve instanceof JvmCyclicClass)
				return new CycPsiClassReferenceType(asPsiClass(((JvmCyclicClass)resolve).getUnderlying()));
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
			return ClassResolveResult.EMPTY;
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
			if(!(o instanceof CycPsiClassReferenceType))
				return false;
			if(!super.equals(o))
				return false;
			
			CycPsiClassReferenceType type = (CycPsiClassReferenceType)o;
			
			return Objects.equals(underlying, type.underlying);
		}
		
		public int hashCode(){
			int result = super.hashCode();
			result = 31 * result + (underlying != null ? underlying.hashCode() : 0);
			return result;
		}
	}
	
	private static class CycAsPsiClass extends LightPsiClassBuilder{
		private final CycType type;
		
		public CycAsPsiClass(CycType type){
			super(type, type.name());
			this.type = type;
		}
		
		public String getQualifiedName(){
			return type.fullyQualifiedName();
		}
		
		public @NotNull PsiElement getNavigationElement(){
			return type;
		}
		
		public @Nullable Icon getIcon(int flags){
			return type.getIcon(flags);
		}
		
		public PsiFile getContainingFile(){
			return type.getContainingFile();
		}
		
		public boolean isInterface(){
			return type.kind() == CycKind.INTERFACE;
		}
		
		public boolean isAnnotationType(){
			return type.kind() == CycKind.ANNOTATION;
		}
		
		public boolean isEnum(){
			return type.kind() == CycKind.ENUM;
		}
	}
}