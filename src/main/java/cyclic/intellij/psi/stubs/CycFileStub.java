package cyclic.intellij.psi.stubs;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import cyclic.intellij.parser.CyclicParserDefinition;
import cyclic.intellij.psi.CycFile;
import org.jetbrains.annotations.NotNull;

public class CycFileStub extends PsiFileStubImpl<CycFile>{
	
	public CycFileStub(CycFile file){
		super(file);
	}
	
	public @NotNull IStubFileElementType<CycFileStub> getType(){
		return CyclicParserDefinition.FILE;
	}
}