package cyclic.intellij.psi.ast;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import cyclic.intellij.psi.CycStubElement;
import cyclic.intellij.psi.stubs.StubCycModifierList;
import cyclic.intellij.psi.stubs.StubTypes;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CycModifierList extends CycStubElement<CycModifierList, StubCycModifierList>{
	
	public CycModifierList(@NotNull ASTNode node){
		super(node);
	}
	
	public CycModifierList(@NotNull StubCycModifierList list){
		super(list, StubTypes.CYC_MODIFIER_LIST);
	}
	
	public boolean hasModifier(String modifier){
		if(getStub() != null)
			return getStub().modifiers().contains(modifier);
		
		return PsiUtils.streamChildrenOfType(this, CycModifier.class).anyMatch(x -> x.textMatches(modifier));
	}
	
	public List<String> getModifiers(){
		if(getStub() != null)
			return getStub().modifiers();
		
		return PsiUtils.streamChildrenOfType(this, CycModifier.class).map(PsiElement::getText).collect(Collectors.toList());
	}
}