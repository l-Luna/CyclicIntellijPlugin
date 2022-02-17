package cyclic.intellij.refactoring;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

public class CycCommenter implements Commenter{
	
	public @Nullable String getLineCommentPrefix(){
		return "//";
	}
	
	public @Nullable String getBlockCommentPrefix(){
		return null;
	}
	
	public @Nullable String getBlockCommentSuffix(){
		return null;
	}
	
	public @Nullable String getCommentedBlockCommentPrefix(){
		return null;
	}
	
	public @Nullable String getCommentedBlockCommentSuffix(){
		return null;
	}
}
