package cyclic.intellij.psi.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import cyclic.intellij.psi.ast.types.CycType;
import cyclic.intellij.psi.stubs.StubCycType;
import cyclic.intellij.psi.stubs.StubTypes;
import cyclic.intellij.psi.types.CycKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StubImplCycType extends StubBase<CycType> implements StubCycType{
	
	@NotNull
	private final String fqName;
	@NotNull
	private final String shortName;
	@NotNull
	private final CycKind kind;
	
	public StubImplCycType(@Nullable StubElement parent, CycType type){
		this(parent, type.getName(), type.getName(), type.kind());
	}
	
	public StubImplCycType(@Nullable StubElement parent, String shortName, String fqName, CycKind kind){
		super(parent, StubTypes.CYC_TYPE);
		this.shortName = shortName;
		this.fqName = fqName;
		this.kind = kind;
	}
	
	@NotNull
	public String fullyQualifiedName(){
		return fqName;
	}
	
	@NotNull
	public String shortName(){
		return shortName;
	}
	
	@NotNull
	public CycKind kind(){
		return kind;
	}
}