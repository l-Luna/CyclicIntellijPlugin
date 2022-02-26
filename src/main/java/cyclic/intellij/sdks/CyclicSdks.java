package cyclic.intellij.sdks;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Service(Service.Level.APP)
@State(name = "CyclicSdks", storages = @Storage("CyclicSdks.xml"))
public final class CyclicSdks implements PersistentStateComponent<CyclicSdks>{
	
	public List<CyclicSdk> compilers = new ArrayList<>();
	
	public static CyclicSdks getInstance(){
		return ApplicationManager.getApplication().getService(CyclicSdks.class);
	}
	
	public @NotNull CyclicSdks getState(){
		return this;
	}
	
	public void loadState(@NotNull CyclicSdks state){
		XmlSerializerUtil.copyBean(state, this);
	}
}