package cyclic.intellij.psi.ast.statements;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiType;
import com.intellij.util.PlatformIcons;
import cyclic.intellij.psi.CycDefinitionAstElement;
import cyclic.intellij.psi.CycVarScope;
import cyclic.intellij.psi.CycVariable;
import cyclic.intellij.psi.ast.CycTypeRef;
import cyclic.intellij.psi.ast.CycTypeRefOrInferred;
import cyclic.intellij.psi.ast.common.CycBlock;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Introduces the catch variable into scope
// Not picked up by CycBlock - not a CycStatement, part of CycTryCatchStatement
public class CycCatchBlock extends CycDefinitionAstElement implements CycVariable, CycVarScope{
	
	public CycCatchBlock(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycStatement> body(){
		return PsiUtils.childOfType(this, CycBlock.class).map(x -> x);
	}
	
	public String varName(){
		return getName();
	}
	
	public JvmType varType(){
		return PsiUtils.childOfType(this, CycTypeRefOrInferred.class)
				.flatMap(CycTypeRefOrInferred::ref)
				.map(CycTypeRef::asType)
				.orElse(PsiType.NULL);
	}
	
	public boolean hasModifier(String modifier){
		return false;
	}
	
	public boolean isLocal(){
		return true;
	}
	
	public List<? extends CycVariable> available(){
		List<CycVariable> superScope = new ArrayList<>(CycVarScope.scopeOf(this).map(CycVarScope::available).orElse(List.of()));
		superScope.add(this);
		return superScope;
	}
	
	public @Nullable Icon getIcon(int flags){
		return PlatformIcons.VARIABLE_ICON;
	}
}