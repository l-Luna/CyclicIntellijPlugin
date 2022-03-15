package cyclic.intellij.presentation.annotators;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.jvm.JvmClassKind;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.psi.PsiElement;
import cyclic.intellij.inspections.fixes.ChangeSupertypeKindFix;
import cyclic.intellij.inspections.fixes.RenameFileToTypeFix;
import cyclic.intellij.inspections.fixes.RenameTypeToFileFix;
import cyclic.intellij.psi.ast.CycTypeRef;
import cyclic.intellij.psi.ast.types.CycExtendsClause;
import cyclic.intellij.psi.ast.types.CycImplementsClause;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.CycKind;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InvalidTypeDefinitionAnnotator implements Annotator{
	
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder){
		if(element instanceof CycType){
			CycType type = (CycType)element;
			// check type name
			if(type.isTopLevelType() && type.getNameIdentifier() != null){
				if(type.getNameIdentifier().getTextLength() > 0){
					String typeName = type.getName();
					if(!typeName.equals(type.getContainingFile().getName()))
						holder.newAnnotation(HighlightSeverity.ERROR,
										"Cyclic type '" + typeName + "' should be declared in file '" + typeName + ".cyc'")
								.withFix(new RenameFileToTypeFix(type, typeName))
								.withFix(new RenameTypeToFileFix(type, type.getContainingFile().getName()))
								.range(type.getNameIdentifier())
								.create();
				}else{
					holder.newAnnotation(HighlightSeverity.ERROR,
									"Cyclic type must have name '" + type.getContainingFile().getName() + "'")
							.create();
				}
			}
			// check supertypes
			boolean[] encountered = {false};
			PsiUtils.childOfType(type, CycExtendsClause.class)
					.map(x -> PsiUtils.childrenOfType(x, CycTypeRef.class))
					.orElse(List.of())
					.forEach(supertype -> {
						if(supertype.getTextLength() > 0){
							var extType = supertype.asType();
							if(extType != null){
								if(!(extType instanceof JvmReferenceType)){
									holder.newAnnotation(HighlightSeverity.ERROR, "Expecting a class, not primitive or array")
											.range(supertype)
											.create();
								}else{
									var extClass = supertype.asClass();
									if(extClass != null){
										if(encountered[0]){
											holder.newAnnotation(HighlightSeverity.ERROR, "Classes cannot extend multiple classes")
													.range(supertype)
													.create();
										}
										encountered[0] = true;
										// TODO: check @AnnotationCanImplement
										if(type.kind() == CycKind.INTERFACE && !(
												extClass.getClassKind() == JvmClassKind.INTERFACE ||
												extClass.getClassKind() == JvmClassKind.ANNOTATION)){
											holder.newAnnotation(HighlightSeverity.ERROR, "Expecting an interface, not a class")
													.range(supertype)
													.create();
										}else if(type.kind() != CycKind.INTERFACE && (
												extClass.getClassKind() == JvmClassKind.INTERFACE ||
												extClass.getClassKind() == JvmClassKind.ANNOTATION)){
											holder.newAnnotation(HighlightSeverity.ERROR, "Expecting a class, not an interface")
													.withFix(new ChangeSupertypeKindFix(supertype, false))
													.range(supertype)
													.create();
										}
										// TODO: check sealed types
										if(extClass.hasModifier(JvmModifier.FINAL))
											holder.newAnnotation(HighlightSeverity.ERROR, "Cannot extend final type")
													.range(supertype)
													.create();
									}
								}
							}
						}
					});
			
			PsiUtils.childOfType(type, CycImplementsClause.class)
					.map(x -> PsiUtils.childrenOfType(x, CycTypeRef.class))
					.orElse(List.of())
					.forEach(supertype -> {
						if(supertype.getTextLength() > 0){
							var implType = supertype.asType();
							if(implType != null){
								if(!(implType instanceof JvmReferenceType)){
									holder.newAnnotation(HighlightSeverity.ERROR, "Expecting an interface, not primitive or array")
											.range(supertype)
											.create();
								}else{
									var implClass = supertype.asClass();
									if(implClass != null){
										if(type.kind() == CycKind.INTERFACE){
											holder.newAnnotation(HighlightSeverity.ERROR, "Interfaces cannot extend types")
													.range(supertype)
													.create();
										}else if(implClass.getClassKind() != JvmClassKind.INTERFACE
												&& implClass.getClassKind() != JvmClassKind.ANNOTATION){
											holder.newAnnotation(HighlightSeverity.ERROR, "Expecting a class, not an interface")
													.withFix(new ChangeSupertypeKindFix(supertype, true))
													.range(supertype)
													.create();
										}
										if(implClass.hasModifier(JvmModifier.FINAL))
											holder.newAnnotation(HighlightSeverity.ERROR, "Cannot implement final type")
													.range(supertype)
													.create();
									}
								}
							}
						}
					});
		}
	}
}