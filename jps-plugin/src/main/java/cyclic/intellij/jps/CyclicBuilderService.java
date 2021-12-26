package cyclic.intellij.jps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.ModuleLevelBuilder;

import java.util.List;

// Unused: Cyclic Compiler requires JDK 16, but IntelliJ only necessarily supports using JDK 11.
// Will be enabled in a future version of the compiler that allows mitigating this, or when IntelliJ supports the new LTS.
public class CyclicBuilderService extends BuilderService{
	
	public @NotNull List<? extends ModuleLevelBuilder> createModuleLevelBuilders(){
		return List.of(new CyclicBuilder());
	}
}