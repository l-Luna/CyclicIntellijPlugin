package cyclic.intellij.presentation.annotators;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.jvm.JvmClassKind;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.lang.jvm.types.JvmReferenceType;
import com.intellij.psi.PsiElement;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.inspections.fixes.ChangeSupertypeKindFix;
import cyclic.intellij.inspections.fixes.RenameFileToTypeFix;
import cyclic.intellij.inspections.fixes.RenameTypeToFileFix;
import cyclic.intellij.psi.ast.CycTypeRef;
import cyclic.intellij.psi.ast.types.CycExtendsClause;
import cyclic.intellij.psi.ast.types.CycImplementsClause;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.types.CycKind;
import cyclic.intellij.psi.types.JvmCyclicClass;
import cyclic.intellij.psi.utils.JvmClassUtils;
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
										CyclicBundle.message("annotator.invalid.type.name", typeName, typeName))
								.withFix(new RenameFileToTypeFix(type, typeName))
								.withFix(new RenameTypeToFileFix(type, type.getContainingFile().getName()))
								.range(type.getNameIdentifier())
								.create();
				}else{
					holder.newAnnotation(HighlightSeverity.ERROR,
									CyclicBundle.message("annotator.missing.type.name", type.getContainingFile().getName()))
							.create();
				}
			}
			// check superclass(es)
			boolean[] encountered = {false};
			PsiUtils.childOfType(type, CycExtendsClause.class)
					.map(x -> PsiUtils.childrenOfType(x, CycTypeRef.class))
					.orElse(List.of())
					.forEach(supertype -> {
						if(supertype.getTextLength() > 0){
							var extType = supertype.asType();
							if(extType != null){
								if(!(extType instanceof JvmReferenceType)){
									holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.invalid.supertype.refClassOnly"))
											.range(supertype)
											.create();
								}else{
									var extClass = supertype.asClass();
									if(extClass != null){
										if(encountered[0] && type.kind() != CycKind.INTERFACE){
											holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.invalid.supertype.oneOnly"))
													.range(supertype)
													.create();
										}
										encountered[0] = true;
										// TODO: check @AnnotationCanImplement
										if(type.kind() == CycKind.INTERFACE && !(
												extClass.getClassKind() == JvmClassKind.INTERFACE ||
												extClass.getClassKind() == JvmClassKind.ANNOTATION)){
											holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.invalid.supertype.interfaceOnly"))
													.range(supertype)
													.create();
										}else if(type.kind() != CycKind.INTERFACE && (
												extClass.getClassKind() == JvmClassKind.INTERFACE ||
												extClass.getClassKind() == JvmClassKind.ANNOTATION)){
											holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.invalid.supertype.classOnly"))
													.withFix(new ChangeSupertypeKindFix(supertype, false))
													.range(supertype)
													.create();
										}
										// TODO: check sealed types
										if(extClass.hasModifier(JvmModifier.FINAL))
											holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.invalid.supertype.extFinal"))
													.range(supertype)
													.create();
									}
								}
							}
						}
					});
			
			// and implemented interfaces
			PsiUtils.childOfType(type, CycImplementsClause.class)
					.map(x -> PsiUtils.childrenOfType(x, CycTypeRef.class))
					.orElse(List.of())
					.forEach(supertype -> {
						if(supertype.getTextLength() > 0){
							var implType = supertype.asType();
							if(implType != null){
								if(!(implType instanceof JvmReferenceType)){
									holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.invalid.supertype.refInterfaceOnly"))
											.range(supertype)
											.create();
								}else{
									var implClass = supertype.asClass();
									if(implClass != null){
										if(type.kind() == CycKind.INTERFACE){
											holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.invalid.supertype.interfaceImpl"))
													.range(supertype)
													.create();
										}else if(implClass.getClassKind() != JvmClassKind.INTERFACE
												&& implClass.getClassKind() != JvmClassKind.ANNOTATION){
											holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.invalid.supertype.interfaceOnly"))
													.withFix(new ChangeSupertypeKindFix(supertype, true))
													.range(supertype)
													.create();
										}
										if(implClass.hasModifier(JvmModifier.FINAL))
											holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message("annotator.invalid.supertype.implFinal"))
													.range(supertype)
													.create();
									}
								}
							}
						}
					});
			
			// and missing abstract methods
			if(type.kind() != CycKind.INTERFACE && !type.hasModifier("abstract")){
				var missing = JvmClassUtils.findUnimplementedMethodsFrom(JvmCyclicClass.of(type), true);
				if(!missing.isEmpty()){
					JvmMethod first = missing.get(0);
					PsiElement identifier = type.getNameIdentifier();
					holder.newAnnotation(HighlightSeverity.ERROR, CyclicBundle.message(
									"annotator.invalid.supertype.mustImplement",
									type.name(),
									JvmClassUtils.summary(first),
									first.getContainingClass().getName()))
							// TODO: stop at opening brace
							.range(identifier != null ? identifier : type)
							.create();
				}
			}
		}
	}
}