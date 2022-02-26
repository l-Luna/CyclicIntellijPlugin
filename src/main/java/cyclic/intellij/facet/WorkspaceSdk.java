package cyclic.intellij.facet;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

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
}