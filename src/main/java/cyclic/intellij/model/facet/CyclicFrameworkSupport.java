package cyclic.intellij.model.facet;

import com.intellij.facet.FacetType;
import com.intellij.facet.ui.FacetBasedFrameworkSupportProvider;
import com.intellij.ide.util.frameworkSupport.FrameworkVersion;
import com.intellij.openapi.roots.ModifiableRootModel;

public class CyclicFrameworkSupport extends FacetBasedFrameworkSupportProvider<CyclicFacet>{
	
	protected CyclicFrameworkSupport(){
		super(FacetType.findInstance(CyclicFacetType.class));
	}
	
	protected void setupConfiguration(CyclicFacet facet, ModifiableRootModel rootModel, FrameworkVersion version){
	
	}
}