package cyclic.intellij.jps;

import cyclic.lang.compiler.CompileTimeException;
import cyclic.lang.compiler.Compiler;
import cyclic.lang.compiler.model.cyclic.CyclicType;
import cyclic.lang.compiler.model.cyclic.CyclicTypeBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CyclicBuilder extends ModuleLevelBuilder{
	
	protected CyclicBuilder(){
		super(BuilderCategory.TRANSLATOR);
	}
	
	public ExitCode build(CompileContext context,
	                      ModuleChunk chunk,
	                      DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder,
	                      OutputConsumer outputConsumer)
			throws ProjectBuildException, IOException{
		AtomicReference<String> message = new AtomicReference<>(null);
		dirtyFilesHolder.processDirtyFiles((target, file, root) -> {
			// TODO: handle dependencies between multiple files
			// requires work in the compiler for supporting arbitrary .class dependencies
			// and compiling an arbitrary set of cyclic files
			String text = Files.readString(file.toPath());
			try{
				var classes = Compiler.compileText(text);
				var cycTypes = CyclicTypeBuilder.fromFile(text);
				for(int i = 0; i < classes.size(); i++){
					CyclicType prototype = cycTypes.get(i);
					outputConsumer.registerCompiledClass(target, new CompiledClass(new File(target.getOutputDir().getPath() + prototype.internalName()), file, prototype.fullyQualifiedName(), new BinaryContent(classes.get(i))));
				}
			}catch(CompileTimeException c){
				message.set(c.getMessage());
				return false;
			}
			return true;
		});
		if(message.get() != null)
			throw new ProjectBuildException("Failed to compile file. " + message.get());
		return ExitCode.OK;
	}
	
	public @NotNull List<String> getCompilableFileExtensions(){
		return List.of("cyc");
	}
	
	public @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String getPresentableName(){
		return "Cyclic";
	}
}
