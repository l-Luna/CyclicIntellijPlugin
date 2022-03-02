package cyclic.intellij.run;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.junit.JavaRunConfigurationProducerBase;
import com.intellij.lang.jvm.util.JvmMainMethodUtil;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import cyclic.intellij.psi.CycElement;
import cyclic.intellij.psi.CycFile;
import cyclic.intellij.psi.elements.CycFileWrapper;
import cyclic.intellij.psi.elements.CycType;
import cyclic.intellij.psi.types.JvmCyclicClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyclicRunConfigProducer extends JavaRunConfigurationProducerBase<ApplicationConfiguration>{
	
	protected boolean setupConfigurationFromContext(@NotNull ApplicationConfiguration configuration,
	                                                @NotNull ConfigurationContext context,
	                                                @NotNull Ref<PsiElement> sourceElement){
		Location<?> loc = context.getLocation();
		if(loc == null)
			return false;
		
		var elem = loc.getPsiElement();
		CycType target = null;
		if(elem instanceof CycFile)
			target = ((CycFile)elem).wrapper().flatMap(CycFileWrapper::getTypeDef).orElse(null);
		if(elem instanceof CycType)
			target = (CycType)elem;
		if(elem instanceof CycElement)
			target = PsiTreeUtil.getParentOfType(elem, CycType.class);
		
		target = filterMainClass(target);
		if(target == null)
			return false;
		
		sourceElement.set(target);
		configuration.setMainClassName(target.fullyQualifiedName());
		configuration.setGeneratedName();
		return true;
	}
	
	public boolean isConfigurationFromContext(@NotNull ApplicationConfiguration configuration, @NotNull ConfigurationContext context){
		PsiElement loc = context.getPsiLocation();
		var type = getMainClassAt(loc);
		// AbstractApplicationConfigurationProducer has more checks than this
		return type != null && type.fullyQualifiedName().equals(configuration.getMainClassName());
	}
	
	public @NotNull ConfigurationFactory getConfigurationFactory(){
		return ApplicationConfigurationType.getInstance().getConfigurationFactories()[0];
	}
	
	public static @Nullable CycType getMainClassAt(PsiElement at){
		if(at instanceof CycFile)
			return ((CycFile)at)
					.wrapper()
					.flatMap(CycFileWrapper::getTypeDef)
					.map(CyclicRunConfigProducer::filterMainClass)
					.orElse(null);
		else
			return filterMainClass(PsiTreeUtil.getParentOfType(at, CycType.class));
	}
	
	public static @Nullable CycType filterMainClass(CycType type){
		if(type == null)
			return null;
		return JvmMainMethodUtil.hasMainMethodInHierarchy(JvmCyclicClass.of(type)) ? type : null;
	}
}