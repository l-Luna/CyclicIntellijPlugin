package cyclic.intellij.model.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NotNull;

public class CyclicFacet extends Facet<CyclicFacetConfiguration>{
	
	public CyclicFacet(@NotNull FacetType facetType, @NotNull Module module, @NotNull @NlsSafe String name, @NotNull CyclicFacetConfiguration configuration, Facet underlyingFacet){
		super(facetType, module, name, configuration, underlyingFacet);
	}
}