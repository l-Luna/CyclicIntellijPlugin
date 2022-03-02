package cyclic.intellij.debug;

import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.impl.DebuggerUtilsAsync;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiFile;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import cyclic.intellij.CyclicFileType;
import cyclic.intellij.psi.CycFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CyclicPositionManager implements PositionManager{
	
	private final DebugProcess process;
	
	public CyclicPositionManager(DebugProcess process){
		this.process = process;
	}
	
	protected String sourcePathByLocation(Location location){
		return location.declaringType().name().replace(".", "/") + ".cyc";
	}
	
	public @Nullable SourcePosition getSourcePosition(@Nullable Location location){
		if(location == null)
			return null;
		String path = sourcePathByLocation(location);
		PsiFile file = CyclicSourcesFinder.findSourceFile(path, process.getProject());
		if(file == null)
			return null;
		return SourcePosition.createFromLine(file, location.lineNumber() - 1);
	}
	
	public @NotNull List<ReferenceType> getAllClasses(@NotNull SourcePosition classPosition) throws NoDataException{
		if(!classPosition.getFile().getFileType().equals(CyclicFileType.FILE_TYPE))
			throw NoDataException.INSTANCE;
		
		// TODO: debug support for inner types
		var defType = ReadAction.compute(() -> ((CycFile)classPosition.getFile()).getTypeDef().orElse(null));
		if(defType != null){
			var qualName = ReadAction.compute(defType::fullyQualifiedName);
			return process.getVirtualMachineProxy().classesByName(qualName);
		}
		
		return List.of();
	}
	
	public @NotNull List<Location> locationsOfLine(@NotNull ReferenceType type, @NotNull SourcePosition position) throws NoDataException{
		if(!position.getFile().getFileType().equals(CyclicFileType.FILE_TYPE))
			throw NoDataException.INSTANCE;
		
		try{
			return DebuggerUtilsAsync.locationsOfLineSync(type, DebugProcess.JAVA_STRATUM, null, position.getLine() + 1);
		}catch(AbsentInformationException ignored){}
		
		return Collections.emptyList();
	}
	
	public @Nullable ClassPrepareRequest createPrepareRequest(@NotNull ClassPrepareRequestor requestor, @NotNull SourcePosition position) throws NoDataException{
		if(!position.getFile().getFileType().equals(CyclicFileType.FILE_TYPE))
			throw NoDataException.INSTANCE;
		
		return ReadAction.compute(() -> {
			var defType = ((CycFile)position.getFile()).getTypeDef().orElse(null);
			if(defType != null)
				return process.getRequestsManager().createClassPrepareRequest(requestor, defType.fullyQualifiedName());
			return null;
		});
	}
}