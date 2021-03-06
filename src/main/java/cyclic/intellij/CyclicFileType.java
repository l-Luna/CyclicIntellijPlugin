package cyclic.intellij;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CyclicFileType extends LanguageFileType{
	
	public static final CyclicFileType FILE_TYPE = new CyclicFileType();
	
	protected CyclicFileType(){
		super(CyclicLanguage.LANGUAGE);
	}
	
	public @NonNls @NotNull String getName(){
		return "Cyclic File";
	}
	
	public @NlsContexts.Label @NotNull String getDescription(){
		return CyclicBundle.message("fileType.desc");
	}
	
	public @NotNull String getDefaultExtension(){
		return "cyc";
	}
	
	public @Nullable Icon getIcon(){
		return CyclicIcons.CYCLIC_FILE;
	}
}