package cyclic.intellij.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.util.IncorrectOperationException;
import cyclic.intellij.CyclicFileType;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.asJvm.AsPsiUtil;
import cyclic.intellij.psi.ast.CycFileWrapper;
import cyclic.intellij.psi.ast.CycImportStatement;
import cyclic.intellij.psi.ast.CycPackageStatement;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CycFile extends PsiFileBase implements PsiClassOwner{
	
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
	
	public PsiClass @NotNull [] getClasses(){
		return getTypeDef()
				.map(AsPsiUtil::asPsiClass)
				.map(x -> new PsiClass[]{x})
				.orElse(PsiClass.EMPTY_ARRAY);
	}
	
	public String getPackageName(){
		return getPackage().map(CycPackageStatement::getPackageName).orElse("");
	}
	
	public void setPackageName(String packageName) throws IncorrectOperationException{
		getPackage()
				.flatMap(CycPackageStatement::getId)
				.ifPresent(x -> x.replace(PsiUtils.createIdFromText(x.getParent(), packageName)));
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
		var typeDef = ReadAction.compute(this::getTypeDef);
		if(typeDef.isPresent())
			return o.substring(0, o.length() - ".cyc".length());
		return o;
	}
}