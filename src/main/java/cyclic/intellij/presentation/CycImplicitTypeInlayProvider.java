package cyclic.intellij.presentation;

import com.intellij.codeInsight.hints.*;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.lang.Language;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPrimitiveType;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.psi.CycVariableDef;
import cyclic.intellij.psi.expressions.CycCastExpr;
import cyclic.intellij.psi.expressions.CycInitialisationExpr;
import cyclic.intellij.psi.expressions.CycLiteralExpr;
import cyclic.intellij.psi.utils.JvmClassUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CycImplicitTypeInlayProvider implements InlayHintsProvider<NoSettings>{
	
	@Nullable
	public InlayHintsCollector getCollectorFor(@NotNull PsiFile file, @NotNull Editor e, @NotNull NoSettings __, @NotNull InlayHintsSink s){
		return new FactoryInlayHintsCollector(e){
			public boolean collect(@NotNull PsiElement element, @NotNull Editor editor, @NotNull InlayHintsSink sink){
				if(element instanceof CycVariableDef){
					CycVariableDef def = (CycVariableDef)element;
					var id = def.getIdentifyingElement();
					
					var init = def.initializer();
					if(!def.isLocalVar() || init.isEmpty() || !def.hasInferredType() || id == null)
						return true;
					var expr = init.get();
					if(expr instanceof CycInitialisationExpr || expr instanceof CycLiteralExpr || expr instanceof CycCastExpr)
						return true;
					var type = expr.type();
					if(type == null || type == PsiPrimitiveType.NULL)
						return true;
					
					var presentation = withColon(type, getFactory());
					var shifted = getFactory().inset(presentation, 3, 0, 0, 0);
					sink.addInlineElement(id.getTextRange().getEndOffset(), true, shifted, false);
				}
				return true;
			}
		};
	}
	
	protected static InlayPresentation withColon(JvmType type, PresentationFactory factory){
		return factory.roundWithBackground(factory.seq(factory.smallText(": "), factory.smallText(JvmClassUtils.name(type))));
	}
	
	public boolean isVisibleInSettings(){
		return true;
	}
	
	@NotNull
	public SettingsKey<NoSettings> getKey(){
		return new SettingsKey<>("cyclic.implicits.types");
	}
	
	@Nls(capitalization = Nls.Capitalization.Sentence)
	@NotNull
	public String getName(){
		return "Implicit Cyclic variable types";
	}
	
	@Nullable
	public String getPreviewText(){
		return null;
	}
	
	@NotNull
	public ImmediateConfigurable createConfigurable(@NotNull NoSettings settings){
		return __ -> new JPanel();
	}
	
	@NotNull
	public NoSettings createSettings(){
		return new NoSettings();
	}
	
	public boolean isLanguageSupported(@NotNull Language language){
		return language == CyclicLanguage.LANGUAGE;
	}
}
