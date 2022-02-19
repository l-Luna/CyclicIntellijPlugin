package cyclic.intellij.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import cyclic.intellij.CyclicFileType;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CycFile extends PsiFileBase{
	
	public CycFile(@NotNull FileViewProvider viewProvider){
		super(viewProvider, CyclicLanguage.LANGUAGE);
	}
	
	public @NotNull FileType getFileType(){
		return CyclicFileType.FILE_TYPE;
	}
	
	public Optional<CycFileWrapper> wrapper(){
		return PsiUtils.childOfType(this, CycFileWrapper.class);
	}
	
	public Optional<CycPackageStatement> getPackage(){
		return wrapper().flatMap(CycFileWrapper::getPackage);
	}
	
	public String getPackageName(){
		return getPackage().map(CycPackageStatement::getPackageName).orElse("");
	}
	
	public List<CycImportStatement> getImports(){
		return wrapper().map(CycFileWrapper::getImports).orElse(new ArrayList<>(0));
	}
	
	public Optional<CycType> getTypeDef(){
		return wrapper().flatMap(CycFileWrapper::getTypeDef);
	}
	
	public @NotNull String getFileName(){
		return getViewProvider().getVirtualFile().getName();
	}
	
	public @NotNull String getName(){
		var o = super.getName();
		if(getTypeDef().isPresent())
			return o.substring(0, o.length() - ".cyc".length());
		return o;
	}
}