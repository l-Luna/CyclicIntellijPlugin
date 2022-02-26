package cyclic.intellij.sdks;

import com.intellij.openapi.util.Version;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VersionConverter extends Converter<Version>{
	
	public @Nullable Version fromString(@NotNull String value){
		return Version.parseVersion(value);
	}
	
	public @Nullable String toString(@NotNull Version value){
		return value.toString();
	}
}