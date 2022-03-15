package cyclic.intellij.psi.ast.types;

import com.intellij.lang.ASTNode;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import cyclic.intellij.psi.CycStubElement;
import cyclic.intellij.psi.ast.CycTypeRef;
import cyclic.intellij.psi.stubs.StubCycClassList;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class CycClassList<CL extends CycClassList<CL>> extends CycStubElement<CL, StubCycClassList<CL>>{
	
	public CycClassList(@NotNull ASTNode node){
		super(node);
	}
	
	public CycClassList(@NotNull StubCycClassList<CL> list, @NotNull IStubElementType<?, ?> nodeType){
		super(list, nodeType);
	}
	
	@NotNull
	public List<JvmClass> elements(){
		return PsiUtils
				.streamChildrenOfType(this, CycTypeRef.class)
				.map(CycTypeRef::asClass)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
	
	@NotNull
	public List<String> elementNames(){
		var stub = getStub();
		if(stub != null)
			return stub.elementFqNames();
		
		return PsiUtils
				.streamChildrenOfType(this, CycTypeRef.class)
				.map(PsiElement::getText)
				.collect(Collectors.toList());
	}
	
	@NotNull
	public Optional<JvmClass> first(){
		return PsiUtils
				.streamChildrenOfType(this, CycTypeRef.class)
				.map(CycTypeRef::asClass)
				.filter(Objects::nonNull)
				.findFirst();
	}
}