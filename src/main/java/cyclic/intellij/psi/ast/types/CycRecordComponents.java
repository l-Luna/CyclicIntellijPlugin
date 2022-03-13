package cyclic.intellij.psi.ast.types;

import com.intellij.lang.ASTNode;
import cyclic.intellij.psi.CycStubElement;
import cyclic.intellij.psi.ast.CycParameter;
import cyclic.intellij.psi.stubs.StubCycRecordComponents;
import cyclic.intellij.psi.stubs.StubTypes;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CycRecordComponents extends CycStubElement<CycRecordComponents, StubCycRecordComponents>{
	
	public CycRecordComponents(@NotNull ASTNode node){
		super(node);
	}
	
	public CycRecordComponents(@NotNull StubCycRecordComponents components){
		super(components, StubTypes.CYC_RECORD_COMPONENTS);
	}
	
	public List<CycParameter> components(){
		return PsiUtils.childrenOfType(this, CycParameter.class);
	}
}