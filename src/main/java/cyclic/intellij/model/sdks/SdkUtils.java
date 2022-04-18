package cyclic.intellij.model.sdks;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Version;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import cyclic.intellij.CyclicBundle;
import cyclic.intellij.model.CyclicLanguageLevel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class SdkUtils{
	
	public static final String PROPERTIES_NAME = "cyclic_compiler.properties";
	
	public static CyclicSdk readJarInfo(String path) throws ConfigurationException{
		return ReadAction.compute(() -> {
			var file = VfsUtil.findFile(Path.of(path), true);
			if(file == null)
				throw new ConfigurationException(CyclicBundle.message("error.sdk.noFile"));
			if(!file.getName().endsWith(".jar"))
				throw new ConfigurationException(CyclicBundle.message("error.sdk.notJar"));
			
			var jarFs = JarFileSystem.getInstance().getJarRootForLocalFile(file);
			if(jarFs == null)
				throw new ConfigurationException(CyclicBundle.message("error.sdk.cant.openJar"));
			
			var compilerProps = jarFs.findChild(PROPERTIES_NAME);
			if(compilerProps == null)
				throw new ConfigurationException(CyclicBundle.message("error.sdk.invalid.noProps"));
			
			String text;
			try{
				text = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(compilerProps.contentsToByteArray())).toString();
			}catch(IOException i){
				throw new ConfigurationException(CyclicBundle.message("error.sdk.cant.readProps"));
			}
			
			String compilerName = null, compilerVersion = null, cyclicVersion = null;
			
			for(String s : text.split("\n")){
				var split = s.split("=");
				if(split.length > 1)
					if(split[0].equals("compiler.name"))
						compilerName = split[1].strip();
					else if(split[0].equals("compiler.version"))
						compilerVersion = split[1].strip();
					else if(split[0].equals("cyclic.version"))
						cyclicVersion = split[1].strip();
			}
			
			if(compilerName == null)
				throw new ConfigurationException(CyclicBundle.message("error.sdk.invalid.noName"));
			if(compilerVersion == null)
				throw new ConfigurationException(CyclicBundle.message("error.sdk.invalid.noCompilerVersion"));
			if(cyclicVersion == null)
				throw new ConfigurationException(CyclicBundle.message("error.sdk.invalid.noLanguageVersion"));
			
			var ver = Version.parseVersion(compilerVersion);
			var languageLevel = CyclicLanguageLevel.getById(cyclicVersion);
			return new CyclicSdk(compilerName, FileUtil.toSystemIndependentName(path), ver != null ? ver : new Version(0, 0, 0), languageLevel);
		});
	}
}