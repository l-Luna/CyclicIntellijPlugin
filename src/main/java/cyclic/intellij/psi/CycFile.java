package cyclic.intellij.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import cyclic.intellij.CyclicFileType;
import cyclic.intellij.CyclicLanguage;
import org.jetbrains.annotations.NotNull;

public class CycFile extends PsiFileBase{
	
	public CycFile(@NotNull FileViewProvider viewProvider){
		super(viewProvider, CyclicLanguage.LANGUAGE);
	}
	
	public @NotNull FileType getFileType(){
		return CyclicFileType.FILE_TYPE;
	}
}
