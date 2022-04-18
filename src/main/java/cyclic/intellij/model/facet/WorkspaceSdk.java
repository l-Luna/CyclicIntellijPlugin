package cyclic.intellij.model.facet;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import cyclic.intellij.model.sdks.CyclicSdk;
import cyclic.intellij.model.sdks.CyclicSdks;
import org.jetbrains.annotations.NotNull;

// TODO: move to module level
@Service(Service.Level.PROJECT)
@State(name = "CyclicProjectSdk", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public final class WorkspaceSdk implements PersistentStateComponent<WorkspaceSdk>{
	
	public String compilerPath = "";
	
	public static WorkspaceSdk getFor(Project p){
		return p.getService(WorkspaceSdk.class);
	}
	
	public @NotNull WorkspaceSdk getState(){
		return this;
	}
	
	public void loadState(@NotNull WorkspaceSdk state){
		XmlSerializerUtil.copyBean(state, this);
	}
	
	public CyclicSdk currentOrDummy(){
		return CyclicSdks.getInstance().compilers.stream()
				.filter(c -> c.path.equals(compilerPath))
				.findFirst()
				.orElse(CyclicSdks.DUMMY_SDK);
	}
}