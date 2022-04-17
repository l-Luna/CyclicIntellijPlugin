package cyclic.intellij.projects.buildSystem;

import com.intellij.ide.util.EditorHelper;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;

public class CyclicModuleBuilder extends JavaModuleBuilder{
	
	public VirtualFile openWhenProjectCreated = null;
	
	protected void setupModule(Module module) throws ConfigurationException{
		ApplicationManager.getApplication().invokeLater(() -> {
			if(openWhenProjectCreated != null){
				var manager = PsiManager.getInstance(module.getProject());
				var psiFile = manager.findFile(openWhenProjectCreated);
				if(psiFile != null)
					EditorHelper.openInEditor(psiFile);
			}
		});
		
		super.setupModule(module);
	}
}