package cyclic.intellij.psi.elements;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CycRecordComponents extends CycElement{
	
	public CycRecordComponents(@NotNull ASTNode node){
		super(node);
	}
	
	public List<CycParameter> components(){
		return PsiUtils.childrenOfType(this, CycParameter.class);
	}
}