package cyclic.intellij.sdks.config;

import com.intellij.openapi.options.ConfigurableWithId;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.Function;
import com.intellij.util.ui.CollectionItemEditor;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.table.TableModelEditor;
import cyclic.intellij.sdks.CyclicSdk;
import cyclic.intellij.sdks.CyclicSdks;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CyclicLanguageConfiguration implements ConfigurableWithId{
	
	private JComponent panel;
	private TableModelEditor<CyclicSdk> configs;
	
	private static final ColumnInfo<?, ?>[] SDK_COLUMNS = {
			new ColumnInfo<CyclicSdk, String>("Name"){
				public @Nullable String valueOf(CyclicSdk sdk){
					return sdk.name;
				}
				
				public boolean isCellEditable(CyclicSdk sdk){
					return false;
				}
			},
			new ColumnInfo<CyclicSdk, String>("Path"){
				public @Nullable String valueOf(CyclicSdk sdk){
					return sdk.path;
				}
				
				public boolean isCellEditable(CyclicSdk sdk){
					return false;
				}
			},
			new ColumnInfo<CyclicSdk, String>("Version"){
				public @Nullable String valueOf(CyclicSdk sdk){
					return sdk.version.toString();
				}
				
				public boolean isCellEditable(CyclicSdk sdk){
					return false;
				}
			}
	};
	
	public @NlsContexts.ConfigurableName String getDisplayName(){
		return "Cyclic";
	}
	
	public @Nullable JComponent createComponent(){
		if(panel == null){
			panel = new JPanel(new BorderLayout());
			var sdks = CyclicSdks.getInstance();
			
			CollectionItemEditor<CyclicSdk> itemEditor = new TableModelEditor.DialogItemEditor<>(){
				public void edit(@NotNull CyclicSdk item, @NotNull Function<? super CyclicSdk, ? extends CyclicSdk> mutator, boolean isAdd){
					var newSettings = item.copy();
					if(ShowSettingsUtil.getInstance().editConfigurable(panel, new CyclicSdkConfigurable(newSettings)))
						mutator.fun(item).copySettings(newSettings);
				}
				
				public void applyEdited(@NotNull CyclicSdk oldItem, @NotNull CyclicSdk newItem){
					oldItem.name = newItem.name;
					oldItem.path = newItem.path;
					oldItem.version = newItem.version;
				}
				
				public @NotNull Class<? extends CyclicSdk> getItemClass(){
					return CyclicSdk.class;
				}
				
				public CyclicSdk clone(@NotNull CyclicSdk item, boolean forInPlaceEditing){
					return item.copy();
				}
				
				public boolean isUseDialogToAdd(){
					return true;
				}
			};
			
			configs = new TableModelEditor<>(
					sdks.compilers,
					SDK_COLUMNS,
					itemEditor,
					"No Cyclic compilers configured");
			
			panel.add(configs.createComponent());
		}
		return panel;
	}
	
	public boolean isModified(){
		CyclicSdks sdks = CyclicSdks.getInstance();
		return configs != null && !configs.apply().equals(sdks.compilers);
	}
	
	public void apply(){
		CyclicSdks sdks = CyclicSdks.getInstance();
		sdks.compilers = configs.apply();
	}
	
	public @NotNull @NonNls String getId(){
		return "cyclicLanguage";
	}
}