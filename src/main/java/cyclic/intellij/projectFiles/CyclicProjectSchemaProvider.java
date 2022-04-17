package cyclic.intellij.projectFiles;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory;
import com.jetbrains.jsonSchema.extension.SchemaType;
import cyclic.intellij.CyclicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CyclicProjectSchemaProvider implements JsonSchemaFileProvider{
	
	public boolean isAvailable(@NotNull VirtualFile file){
		return ProjectFileUtil.isProjectFile(file);
	}
	
	public @NotNull @Nls String getName(){
		return CyclicBundle.message("schema.cyclicProject");
	}
	
	public @Nullable VirtualFile getSchemaFile(){
		return JsonSchemaProviderFactory.getResourceFile(CyclicProjectSchemaProvider.class, "/schemas/cyclicProjectSchema.json");
	}
	
	public @NotNull SchemaType getSchemaType(){
		return SchemaType.schema; // maybe `embeddedSchema` would be more correct?
	}
	
	public static class Factory implements JsonSchemaProviderFactory, DumbAware{
		
		public @NotNull List<JsonSchemaFileProvider> getProviders(@NotNull Project project){
			return List.of(new CyclicProjectSchemaProvider());
		}
	}
}