package cyclic.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CycPackageStatement extends CycElement{
	
	public CycPackageStatement(@NotNull ASTNode node){
		super(node);
	}
	
	@Nullable
	public String getPackageName(){
		CycId id = PsiTreeUtil.findChildOfType(this, CycId.class);
		if(id != null)
			return id.getText();
		return null;
	}
	
	// TODO: resolve against packages. maybe look at JvmPackage, PriDirectoryContainer?
}
