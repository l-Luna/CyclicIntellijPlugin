package cyclic.intellij.asJvm;

import com.intellij.lang.jvm.types.JvmArrayType;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightMethodBuilder;
import com.intellij.psi.impl.light.LightPsiClassBuilder;
import com.intellij.psi.search.GlobalSearchScope;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.psi.elements.CycMethod;
import cyclic.intellij.psi.elements.CycParameter;
import cyclic.intellij.psi.elements.CycType;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.JvmClassUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AsPsiUtil{
	
	public static PsiClass asPsiClass(CycType type){
		var builder = new LightPsiClassBuilder(type, type.name()){
			public String getQualifiedName(){
				return type.fullyQualifiedName();
			}
		};
		
		for(CycMethod method : type.methods()){
			var mBuilder = new LightMethodBuilder(builder.getManager(), CyclicLanguage.LANGUAGE, method.getName());
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
			
			builder.addMethod(mBuilder);
		}
		// TODO: extends/implements lists, fields (on IJ's side)
		return builder;
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
}