package cyclic.intellij.debug;

import com.intellij.debugger.PositionManager;
import com.intellij.debugger.PositionManagerFactory;
import com.intellij.debugger.engine.DebugProcess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicPosManagerFactory extends PositionManagerFactory{
	
	public @Nullable PositionManager createPositionManager(@NotNull DebugProcess process){
		return new CyclicPositionManager(process);
	}
}