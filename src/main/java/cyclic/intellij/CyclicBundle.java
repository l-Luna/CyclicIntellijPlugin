package cyclic.intellij;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.function.Supplier;

public class CyclicBundle extends DynamicBundle{
	
	public static final String BUNDLE = "messages.CyclicBundle";
	private static final CyclicBundle INSTANCE = new CyclicBundle();
	
	public CyclicBundle(){
		super(BUNDLE);
	}
	
	@NotNull
	public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params){
		return INSTANCE.getMessage(key, params);
	}
	
	public static @Nls String partialMessage(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
	                                         int unassignedParams,
	                                         Object @NotNull ... params){
		return INSTANCE.getPartialMessage(key, unassignedParams, params);
	}
	
	@NotNull
	public static Supplier<@Nls String> messagePointer(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params){
		return INSTANCE.getLazyMessage(key, params);
	}
}