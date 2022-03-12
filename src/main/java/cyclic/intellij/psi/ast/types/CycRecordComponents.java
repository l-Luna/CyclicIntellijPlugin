package cyclic.intellij.psi.ast.types;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycAstElement;
import cyclic.intellij.psi.ast.CycParameter;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CycRecordComponents extends CycAstElement{
	
	public CycRecordComponents(@NotNull ASTNode node){
		super(node);
	}
	
	public List<CycParameter> components(){
		return PsiUtils.childrenOfType(this, CycParameter.class);
	}
}