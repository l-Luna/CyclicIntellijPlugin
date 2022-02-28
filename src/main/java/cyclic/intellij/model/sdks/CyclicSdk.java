package cyclic.intellij.model.sdks;

import com.intellij.openapi.util.Version;
import com.intellij.util.xmlb.annotations.OptionTag;

public class CyclicSdk{
	
	public String name = "";
	public String path = "";
	
	@OptionTag(converter = VersionConverter.class)
	public Version version = new Version(0, 0, 0);
	
	// by reflection
	@SuppressWarnings("unused")
	public CyclicSdk(){}
	
	public CyclicSdk(String name, String path, Version version){
		this.name = name;
		this.path = path;
		this.version = version;
	}
	
	public CyclicSdk copy(){
		return new CyclicSdk(name, path, version);
	}
	
	public void copySettings(CyclicSdk other){
		name = other.name;
		path = other.path;
		version = other.version;
	}
	
	public boolean equals(Object o){
		if(this == o)
			return true;
		if(!(o instanceof CyclicSdk))
			return false;
		
		CyclicSdk sdk = (CyclicSdk)o;
		
		if(!name.equals(sdk.name))
			return false;
		if(!path.equals(sdk.path))
			return false;
		return version.equals(sdk.version);
	}
	
	public int hashCode(){
		int result = name.hashCode();
		result = 31 * result + path.hashCode();
		result = 31 * result + version.hashCode();
		return result;
	}
}