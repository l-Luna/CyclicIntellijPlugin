package cyclic.intellij.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import cyclic.intellij.psi.ast.types.CycClassList;
import cyclic.intellij.psi.ast.types.CycType;

public final class StubIndexes{
	
	public static final StubIndexKey<String, CycType> TYPES_BY_FQ_NAME = StubIndexKey.createIndexKey("cyclic.type.fq");
	public static final StubIndexKey<String, CycType> TYPES_BY_SHORT_NAME = StubIndexKey.createIndexKey("cyclic.type.short");
	
	public static final StubIndexKey<String, CycClassList<?>> INHERITANCE_LISTS = StubIndexKey.createIndexKey("cyclic.inheritance");
}