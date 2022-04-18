package cyclic.intellij.model;

import java.util.List;

public enum CyclicLanguageLevel{
	
	// TODO: i18n, track what features are supported by each language level

	v0_1_0("0.1.0", "(Plain Cyclic)");
	
	private final String id;
	private final String presentableName;
	private final String description;
	
	private static final List<CyclicLanguageLevel> ENTRIES = List.of(values());
	
	CyclicLanguageLevel(String id, String presentableName, String description){
		this.id = id;
		this.presentableName = presentableName;
		this.description = description;
	}
	
	CyclicLanguageLevel(String id, String description){
		this(id, id, description);
	}
	
	public String getId(){
		return id;
	}
	
	public String getPresentableName(){
		return presentableName;
	}
	
	public String getDescription(){
		return description;
	}
	
	public static CyclicLanguageLevel getById(String id){
		for(CyclicLanguageLevel level : values())
			if(level.getId().equals(id))
				return level;
		return null;
	}
	
	public static List<CyclicLanguageLevel> entries(){
		return ENTRIES;
	}
}