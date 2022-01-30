package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CycFileWrapper extends CycElement{
	
	public CycFileWrapper(@NotNull ASTNode node){
		super(node);
	}
	
	public Optional<CycPackageStatement> getPackage(){
		return PsiUtils.childOfType(this, CycPackageStatement.class);
	}
	
	public List<CycImportStatement> getImports(){
		return new ArrayList<>(PsiTreeUtil.findChildrenOfType(this, CycImportStatement.class));
	}
	
	public Optional<CycType> getTypeDef(){
		return PsiUtils.childOfType(this, CycType.class);
	}
}