package cyclic.intellij.model.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import cyclic.intellij.CyclicIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CyclicFacetType extends FacetType<CyclicFacet, CyclicFacetConfiguration>{
	
	public static final String ID = "CyclicFacet";
	public static final String NAME = "Cyclic";
	
	public static final FacetTypeId<CyclicFacet> FC_ID = new FacetTypeId<>(ID);
	
	public CyclicFacetType(){
		super(FC_ID, ID, NAME);
	}
	
	public CyclicFacetConfiguration createDefaultConfiguration(){
		return new CyclicFacetConfiguration();
	}
	
	public CyclicFacet createFacet(@NotNull Module module, String name, @NotNull CyclicFacetConfiguration conf, @Nullable Facet underlying){
		return new CyclicFacet(this, module, name, conf, underlying);
	}
	
	public boolean isSuitableModuleType(ModuleType moduleType){
		return moduleType instanceof JavaModuleType;
	}
	
	public @Nullable Icon getIcon(){
		return CyclicIcons.CYCLIC_ICON;
	}
}