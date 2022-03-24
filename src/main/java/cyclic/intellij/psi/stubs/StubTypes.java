package cyclic.intellij.psi.stubs;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.*;
import cyclic.intellij.CyclicLanguage;
import cyclic.intellij.antlr_generated.CyclicLangParser;
import cyclic.intellij.psi.Tokens;
import cyclic.intellij.psi.ast.CycMethod;
import cyclic.intellij.psi.ast.CycModifierList;
import cyclic.intellij.psi.ast.CycParametersList;
import cyclic.intellij.psi.ast.CycTypeRef;
import cyclic.intellij.psi.ast.common.CycParameter;
import cyclic.intellij.psi.ast.common.CycVariableDef;
import cyclic.intellij.psi.ast.types.*;
import cyclic.intellij.psi.indexes.StubIndexes;
import cyclic.intellij.psi.stubs.impl.*;
import cyclic.intellij.psi.types.CycKind;
import cyclic.intellij.psi.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public interface StubTypes{
	
	IStubElementType<StubCycType, CycType> CYC_TYPE = new CycTypeStubElementType();
	IStubElementType<StubCycMemberWrapper, CycMemberWrapper> CYC_MEMBER = new CycMemberStubElementType();
	IStubElementType<StubCycRecordComponents, CycRecordComponents> CYC_RECORD_COMPONENTS = new CycRecordComponentsStubElementType();
	IStubElementType<StubCycParameter, CycParameter> CYC_PARAMETER = new CycParameterStubElementType();
	IStubElementType<StubCycModifierList, CycModifierList> CYC_MODIFIER_LIST = new CycModifierListStubElementType();
	IStubElementType<StubCycMethod, CycMethod> CYC_METHOD = new CycMethodStubElementType();
	IStubElementType<StubCycField, CycVariableDef> CYC_FIELD = new CycFieldStubElementType();
	
	IStubElementType<StubCycClassList<CycExtendsClause>, CycExtendsClause> CYC_EXTENDS_LIST
			= new CycClassListStubElementType<>("CYC_EXTENDS_LIST", CycExtendsClause::new);
	IStubElementType<StubCycClassList<CycImplementsClause>, CycImplementsClause> CYC_IMPLEMENTS_LIST
			= new CycClassListStubElementType<>("CYC_IMPLEMENTS_LIST", CycImplementsClause::new);
	IStubElementType<StubCycClassList<CycPermitsClause>, CycPermitsClause> CYC_PERMITS_LIST
			= new CycClassListStubElementType<>("CYC_PERMITS_LIST", CycPermitsClause::new);
	
	IStubElementType<EmptyStub<?>, CycParametersList> CYC_PARAMETERS_LIST
			= new EmptyStubElementType<>("CYC_PARAMETERS_LIST", CyclicLanguage.LANGUAGE){
		@SuppressWarnings("unchecked")
		public CycParametersList createPsi(@NotNull EmptyStub<?> stub){
			return new CycParametersList((EmptyStub<CycParametersList>)stub);
		}
		public @NotNull String getExternalId(){
			return "cyclic." + this;
		}
	};
	
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
	
	class CycMemberStubElementType extends IStubElementType<StubCycMemberWrapper, CycMemberWrapper>{
		
		public CycMemberStubElementType(){
			super("CYC_MEMBER", CyclicLanguage.LANGUAGE);
		}
		
		public CycMemberWrapper createPsi(@NotNull StubCycMemberWrapper stub){
			return new CycMemberWrapper(stub);
		}
		
		public @NotNull StubCycMemberWrapper createStub(@NotNull CycMemberWrapper psi, StubElement<? extends PsiElement> parent){
			return new StubImplCycMemberWrapper(parent);
		}
		
		public @NotNull String getExternalId(){
			return "cyclic." + this;
		}
		
		public void serialize(@NotNull StubCycMemberWrapper stub, @NotNull StubOutputStream dataStream){}
		
		public @NotNull StubCycMemberWrapper deserialize(@NotNull StubInputStream dataStream, StubElement parent){
			return new StubImplCycMemberWrapper(parent);
		}
		
		public void indexStub(@NotNull StubCycMemberWrapper stub, @NotNull IndexSink sink){}
	}
	
	class CycRecordComponentsStubElementType extends IStubElementType<StubCycRecordComponents, CycRecordComponents>{
		
		public CycRecordComponentsStubElementType(){
			super("CYC_RECORD_COMPONENTS", CyclicLanguage.LANGUAGE);
		}
		
		public CycRecordComponents createPsi(@NotNull StubCycRecordComponents stub){
			return new CycRecordComponents(stub);
		}
		
		public @NotNull StubCycRecordComponents createStub(@NotNull CycRecordComponents psi, StubElement<? extends PsiElement> parent){
			return new StubImplCycRecordComponents(parent);
		}
		
		public @NotNull String getExternalId(){
			return "cyclic." + this;
		}
		
		public void serialize(@NotNull StubCycRecordComponents stub, @NotNull StubOutputStream stream){}
		
		public @NotNull StubCycRecordComponents deserialize(@NotNull StubInputStream stream, StubElement parentStub){
			return new StubImplCycRecordComponents(parentStub);
		}
		
		public void indexStub(@NotNull StubCycRecordComponents stub, @NotNull IndexSink sink){}
	}
	
	class CycParameterStubElementType extends IStubElementType<StubCycParameter, CycParameter>{
		
		public CycParameterStubElementType(){
			super("CYC_PARAMETER", CyclicLanguage.LANGUAGE);
		}
		
		public CycParameter createPsi(@NotNull StubCycParameter stub){
			return new CycParameter(stub);
		}
		
		public @NotNull StubCycParameter createStub(@NotNull CycParameter psi, StubElement<? extends PsiElement> parent){
			String name = psi.varName(), type = psi.getTypeName().map(PsiElement::getText).orElse("");
			boolean varargs = psi.isVarargs();
			return new StubImplCycParameter(parent, name, type, varargs);
		}
		
		public @NotNull String getExternalId(){
			return "cyclic." + this;
		}
		
		public void serialize(@NotNull StubCycParameter stub, @NotNull StubOutputStream stream) throws IOException{
			stream.writeName(stub.name());
			stream.writeName(stub.typeText());
			stream.writeBoolean(stub.isVarargs());
		}
		
		public @NotNull StubCycParameter deserialize(@NotNull StubInputStream stream, StubElement parent) throws IOException{
			var name = stream.readNameString();
			var type = stream.readNameString();
			return new StubImplCycParameter(parent, name != null ? name : "", type != null ? type : "", stream.readBoolean());
		}
		
		public void indexStub(@NotNull StubCycParameter stub, @NotNull IndexSink sink){
			if(stub.isRecordComponent())
				sink.occurrence(StubIndexes.FIELDS, stub.name());
		}
	}
	
	class CycModifierListStubElementType extends IStubElementType<StubCycModifierList, CycModifierList>{
		
		public CycModifierListStubElementType(){
			super("CYC_MODIFIER_LIST", CyclicLanguage.LANGUAGE);
		}
		
		public CycModifierList createPsi(@NotNull StubCycModifierList stub){
			return new CycModifierList(stub);
		}
		
		public @NotNull StubCycModifierList createStub(@NotNull CycModifierList psi, StubElement<? extends PsiElement> parent){
			return new StubImplCycModifierList(parent, Collections.unmodifiableList(psi.getModifiers()));
		}
		
		public @NotNull String getExternalId(){
			return "cyclic." + this;
		}
		
		public void serialize(@NotNull StubCycModifierList stub, @NotNull StubOutputStream stream) throws IOException{
			var list = stub.modifiers();
			stream.writeVarInt(list.size());
			for(String mod : list)
				stream.writeName(mod);
		}
		
		public @NotNull StubCycModifierList deserialize(@NotNull StubInputStream stream, StubElement parent) throws IOException{
			int amount = stream.readVarInt();
			List<String> modifiers = new ArrayList<>(amount);
			for(int i = 0; i < amount; i++)
				modifiers.add(stream.readNameString());
			return new StubImplCycModifierList(parent, modifiers);
		}
		
		public void indexStub(@NotNull StubCycModifierList stub, @NotNull IndexSink sink){}
	}
	
	class CycMethodStubElementType extends IStubElementType<StubCycMethod, CycMethod>{
		
		public CycMethodStubElementType(){
			super("CYC_METHOD", CyclicLanguage.LANGUAGE);
		}
		
		public CycMethod createPsi(@NotNull StubCycMethod stub){
			return new CycMethod(stub);
		}
		
		public @NotNull StubCycMethod createStub(@NotNull CycMethod psi, StubElement<? extends PsiElement> parent){
			return new StubImplCycMethod(parent, psi.getName(), psi.returns().map(PsiElement::getText).orElse(""), psi.hasSemicolon());
		}
		
		public @NotNull String getExternalId(){
			return "cyclic." + this;
		}
		
		public void serialize(@NotNull StubCycMethod stub, @NotNull StubOutputStream stream) throws IOException{
			stream.writeName(stub.name());
			stream.writeName(stub.returnTypeText());
			stream.writeBoolean(stub.hasSemicolon());
		}
		
		public @NotNull StubCycMethod deserialize(@NotNull StubInputStream stream, StubElement parent) throws IOException{
			return new StubImplCycMethod(parent, stream.readNameString(), stream.readNameString(), stream.readBoolean());
		}
		
		public void indexStub(@NotNull StubCycMethod stub, @NotNull IndexSink sink){
			sink.occurrence(StubIndexes.METHODS, stub.name());
		}
	}
	
	class CycFieldStubElementType extends IStubElementType<StubCycField, CycVariableDef>{
		
		public CycFieldStubElementType(){
			super("CYC_FIELD", CyclicLanguage.LANGUAGE);
		}
		
		public CycVariableDef createPsi(@NotNull StubCycField stub){
			return new CycVariableDef(stub);
		}
		
		public @NotNull StubCycField createStub(@NotNull CycVariableDef psi, StubElement<? extends PsiElement> parent){
			return new StubImplCycField(
					parent,
					psi.varName(),
					PsiUtils.childOfType(psi, CycTypeRef.class).map(PsiElement::getText).orElse(""));
		}
		
		public @NotNull String getExternalId(){
			return "cyclic." + this;
		}
		
		public void serialize(@NotNull StubCycField stub, @NotNull StubOutputStream stream) throws IOException{
			stream.writeName(stub.name());
			stream.writeName(stub.typeText());
		}
		
		public @NotNull StubCycField deserialize(@NotNull StubInputStream stream, StubElement parent) throws IOException{
			var name = stream.readNameString();
			var type = stream.readNameString();
			return new StubImplCycField(parent, name != null ? name : "", type != null ? type : "");
		}
		
		public void indexStub(@NotNull StubCycField stub, @NotNull IndexSink sink){
			sink.occurrence(StubIndexes.FIELDS, stub.name());
		}
		
		public boolean shouldCreateStub(ASTNode node){
			// only fields, not locals
			return node.getTreeParent().getElementType() == Tokens.getRuleFor(CyclicLangParser.RULE_member);
		}
	}
}