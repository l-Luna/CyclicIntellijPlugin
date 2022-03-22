package cyclic.intellij.presentation.find;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangLexer;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.ast.CycTypeRefOrInferred;
import cyclic.intellij.psi.utils.CycVariable;
import cyclic.intellij.psi.utils.JvmClassUtils;
import org.jetbrains.annotations.Nullable;

public class CyclicGotoVarTypeHandler extends GotoDeclarationHandlerBase{
	
	public @Nullable PsiElement getGotoDeclarationTarget(@Nullable PsiElement element, Editor editor){
		if(element == null || !element.getLanguage().isKindOf(CyclicLanguage.LANGUAGE))
			return null;
		IElementType type = element.getNode().getElementType();
		if(!(type.equals(Tokens.getFor(CyclicLangLexer.VAR)) || type.equals(Tokens.getFor(CyclicLangLexer.VAL))))
			return null;
		
		var ref = PsiTreeUtil.getParentOfType(element, CycTypeRefOrInferred.class);
		if(ref == null)
			return null;
		
		var variable = PsiTreeUtil.getParentOfType(ref, CycVariable.class);
		if(variable == null)
			return null;
		
		var target = JvmClassUtils.asClass(variable.varType());
		return target != null ? target.getSourceElement() : null;
	}
}