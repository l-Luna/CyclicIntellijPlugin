package cyclic.intellij.model.sdks;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Version;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import cyclic.intellij.CyclicBundle;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class SdkUtils{
	
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
			
			var compilerProps = jarFs.findChild("info.properties");
			if(compilerProps == null)
				throw new ConfigurationException(CyclicBundle.message("error.sdk.invalid.noProps"));
			
			// this *can't* be the best way.
			String text;
			try{
				text = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(compilerProps.contentsToByteArray())).toString();
			}catch(IOException i){
				throw new ConfigurationException(CyclicBundle.message("error.sdk.cant.readProps"));
			}
			
			String name = null, version = null;
			
			for(String s : text.split("\n")){
				var split = s.split("=");
				if(split.length > 1)
					if(split[0].equals("name"))
						name = split[1].strip();
					else if(split[0].equals("version"))
						version = split[1].strip();
			}
			
			if(name == null)
				throw new ConfigurationException(CyclicBundle.message("error.sdk.invalid.noName"));
			if(version == null)
				throw new ConfigurationException(CyclicBundle.message("error.sdk.invalid.noVersion"));
			
			var ver = Version.parseVersion(version);
			return new CyclicSdk(name, FileUtil.toSystemIndependentName(path), ver != null ? ver : new Version(0, 0, 0));
		});
	}
}