package cyclic.intellij.model.config;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.ComboBox;
import cyclic.intellij.model.CyclicLanguageLevel;
import cyclic.intellij.model.sdks.CyclicSdk;
import cyclic.intellij.model.sdks.CyclicSdks;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CompilerOptions{
	
	public static ComboBox<CyclicLanguageLevel> languageLevelChooser(){
		var box = new ComboBox<>(CyclicLanguageLevel.values());
		box.setRenderer(new LanguageLevelCellRenderer());
		return box;
	}
	
	public static ComboBox<CyclicSdk> sdkChooser(@Nullable CyclicSdk current){
		List<CyclicSdk> toShow = new ArrayList<>(CyclicSdks.getInstance().compilers);
		if(current != null && !toShow.contains(current))
			toShow.add(0, current);
		if(!toShow.contains(CyclicSdks.DUMMY_SDK))
			toShow.add(0, CyclicSdks.DUMMY_SDK);
		
		var box = new ComboBox<>(toShow.toArray(CyclicSdk[]::new));
		box.setRenderer(new CyclicSdkCellRenderer());
		if(current != null)
			box.setSelectedItem(current);
		
		return box;
	}
	
	public static class LanguageLevelCellRenderer extends DefaultListCellRenderer{
		
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if(value instanceof CyclicLanguageLevel){
				CyclicLanguageLevel ll = (CyclicLanguageLevel)value;
				setText("<html>" + ll.getPresentableName() + " <font color='gray'>" + ll.getDescription() + "</font></html>");
			}
			return component;
		}
	}
	
	public static class CyclicSdkCellRenderer extends DefaultListCellRenderer{
		
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if(value == CyclicSdks.DUMMY_SDK){
				setText("<html><font color='red'>&lt;No SDK&gt;</font></html>");
				setIcon(AllIcons.General.Error);
				return component;
			}
			if(value instanceof CyclicSdk){
				CyclicSdk sdk = (CyclicSdk)value;
				setText("<html>" + sdk.name + " <font color='gray'>- " + sdk.version + "</font></html>");
				setIcon(AllIcons.Nodes.PpLib);
			}
			return component;
		}
	}
}