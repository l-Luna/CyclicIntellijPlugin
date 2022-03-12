package cyclic.intellij.psi.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.*;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.psi.ast.types.*;
import cyclic.intellij.psi.indexes.StubIndexes;
import cyclic.intellij.psi.stubs.impl.StubImplCycClassList;
import cyclic.intellij.psi.stubs.impl.StubImplCycType;
import cyclic.intellij.psi.types.CycKind;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

public interface StubTypes{
	
	IStubElementType<StubCycType, CycType> CYC_TYPE = new CycTypeStubElementType();
	
	IStubElementType<StubCycClassList<CycExtendsClause>, CycExtendsClause> CYC_EXTENDS_LIST
			= new CycClassListStubElementType<>("CYC_EXTENDS_LIST", CycExtendsClause::new);
	IStubElementType<StubCycClassList<CycImplementsClause>, CycImplementsClause> CYC_IMPLEMENTS_LIST
			= new CycClassListStubElementType<>("CYC_IMPLEMENTS_LIST", CycImplementsClause::new);
	IStubElementType<StubCycClassList<CycPermitsClause>, CycPermitsClause> CYC_PERMITS_LIST
			= new CycClassListStubElementType<>("CYC_PERMITS_LIST", CycPermitsClause::new);
	
	class CycTypeStubElementType extends IStubElementType<StubCycType, CycType>{
		
		public CycTypeStubElementType(){
			super("CYC_TYPE", CyclicLanguage.LANGUAGE);
		}
		
		public CycType createPsi(@NotNull StubCycType stub){
			return new CycType(stub);
		}
		
		public @NotNull StubCycType createStub(@NotNull CycType psi, StubElement<? extends PsiElement> parent){
			var stub = psi.getStub();
			return stub != null ? stub : new StubImplCycType(parent, psi);
		}
		
		public @NotNull String getExternalId(){
			return "cyclic." + this;
		}
		
		public void serialize(@NotNull StubCycType stub, @NotNull StubOutputStream stream) throws IOException{
			stream.writeName(stub.shortName());
			stream.writeName(stub.fullyQualifiedName());
			stream.writeVarInt(stub.kind().ordinal());
		}
		
		public @NotNull StubCycType deserialize(@NotNull StubInputStream stream, StubElement parent) throws IOException{
			return new StubImplCycType(parent,
					stream.readNameString(),
					stream.readNameString(),
					CycKind.values()[stream.readVarInt()]);
		}
		
		public void indexStub(@NotNull StubCycType stub, @NotNull IndexSink sink){
			sink.occurrence(StubIndexes.TYPES_BY_FQ_NAME, stub.fullyQualifiedName());
			sink.occurrence(StubIndexes.TYPES_BY_SHORT_NAME, stub.shortName());
		}
	}
	
	class CycClassListStubElementType<CL extends CycClassList<CL>> extends IStubElementType<StubCycClassList<CL>, CL>{
		
		private final Function<StubCycClassList<CL>, CL> builder;
		
		public CycClassListStubElementType(String name, Function<StubCycClassList<CL>, CL> builder){
			super(name, CyclicLanguage.LANGUAGE);
			this.builder = builder;
		}
		
		public CL createPsi(@NotNull StubCycClassList<CL> stub){
			return builder.apply(stub);
		}
		
		public @NotNull StubCycClassList<CL> createStub(@NotNull CL psi, StubElement<? extends PsiElement> parent){
			var stub = psi.getStub();
			return stub != null ? stub : new StubImplCycClassList<>(parent, this, psi.elementNames());
		}
		
		public @NotNull String getExternalId(){
			return "cyclic." + this;
		}
		
		public void serialize(@NotNull StubCycClassList<CL> stub, @NotNull StubOutputStream stream) throws IOException{
			var names = stub.elementFqNames();
			stream.writeVarInt(names.size());
			for(String name : names)
				stream.writeName(name);
		}
		
		public @NotNull StubCycClassList<CL> deserialize(@NotNull StubInputStream stream, StubElement parent) throws IOException{
			int size = stream.readVarInt();
			var list = new ArrayList<String>(size);
			for(int i = 0; i < size; i++)
				list.add(stream.readNameString());
			return new StubImplCycClassList<>(parent, this, list);
		}
		
		@SuppressWarnings("EqualsBetweenInconvertibleTypes")
		public void indexStub(@NotNull StubCycClassList<CL> stub, @NotNull IndexSink sink){
			if(this.equals(CYC_EXTENDS_LIST) || this.equals(CYC_IMPLEMENTS_LIST))
				for(String name : stub.elementFqNames())
					sink.occurrence(StubIndexes.INHERITANCE_LISTS, name);
		}
	}
}