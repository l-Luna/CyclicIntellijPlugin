package cyclic.intellij.projects;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLElementGenerator;
import org.jetbrains.yaml.psi.*;

import java.util.Set;

import static cyclic.intellij.projects.CyclicProjectYamlFileIconPatcher.PROJECT_YAML_EXTENSION;

public class CyclicProjectPathReferenceContributor extends PsiReferenceContributor{
	
	public static final Set<String> TOP_LEVEL_PATH_ELEMENT_NAMES = Set.of("source", "output");
	public static final Set<String> PACKAGE_PATH_ELEMENT_NAMES = Set.of("location");
	public static final Set<String> PACKAGE_LIST_NAME = Set.of("packages", "dependencies");
	
	public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar){
		registrar.registerReferenceProvider(PlatformPatterns
						.psiElement(YAMLKeyValue.class)
						.inVirtualFile(PlatformPatterns.virtualFile().withName(StandardPatterns.string().endsWith(PROJECT_YAML_EXTENSION))),
				new CyclicProjectPathReferenceProvider());
	}
	
	private static class CyclicProjectPathReferenceProvider extends PsiReferenceProvider{
		public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context){
			//for a top level ref: key/value -> mapping -> document -> file
			//for a package ref: key/value -> mapping -> sequence item -> sequence (packages/dependencies) -> key/value -> mapping -> document -> file
			// I'm sure there's a better way to do this
			if(element instanceof YAMLKeyValue){
				YAMLKeyValue entry = (YAMLKeyValue)element;
				if(!(entry.getValue() instanceof YAMLScalar))
					return PsiReference.EMPTY_ARRAY;
				YAMLScalar value = (YAMLScalar)entry.getValue();
				String key = entry.getKeyText();
				if(TOP_LEVEL_PATH_ELEMENT_NAMES.contains(key)){
					if(entry.getParent() instanceof YAMLMapping){
						YAMLMapping topMapping = (YAMLMapping)entry.getParent();
						if(topMapping.getParent() instanceof YAMLDocument)
							return referencesFor(value, entry);
					}
				}else if(PACKAGE_PATH_ELEMENT_NAMES.contains(key)){
					if(entry.getParent() instanceof YAMLMapping){
						var packageMapping = (YAMLMapping)entry.getParent();
						if(packageMapping.getParent() instanceof YAMLSequenceItem){
							var packagesSequenceItem = (YAMLSequenceItem)packageMapping.getParent();
							if(packagesSequenceItem.getParent() instanceof YAMLSequence){
								var sequence = (YAMLSequence)packagesSequenceItem.getParent();
								if(sequence.getParent() instanceof YAMLKeyValue){
									var packagesSequenceEntry = (YAMLKeyValue)sequence.getParent();
									if(PACKAGE_LIST_NAME.contains(packagesSequenceEntry.getKeyText())){
										if(packagesSequenceEntry.getParent() instanceof YAMLMapping){
											var topMapping = (YAMLMapping)packagesSequenceEntry.getParent();
											if(topMapping.getParent() instanceof YAMLDocument)
												return referencesFor(value, entry);
										}
									}
								}
							}
						}
					}
				}
			}
			return new PsiReference[0];
		}
		
		private PsiReference[] referencesFor(YAMLScalar value, YAMLKeyValue entry){
			return new YamlTextFileReferenceSet(
					value.getTextValue(),
					entry,
					value.getStartOffsetInParent() + 1,
					this).getAllReferences();
		}
	}
	
	// the default file reference set fails to properly rename path elements, since it attempts to change keys, not values
	private static class YamlTextFileReferenceSet extends FileReferenceSet{
		
		YamlTextFileReferenceSet(String text, PsiElement element, int startOffset, PsiReferenceProvider provider){
			super(text, element, startOffset, provider, true, false);
		}
		
		public FileReference createFileReference(final TextRange range, final int index, final String text){
			return new YamlTextFileReference(this, range, index, text);
		}
	}
	
	private static class YamlTextFileReference extends FileReference{
		
		private static final Logger LOG = Logger.getInstance(YamlTextFileReferenceSet.class);
		private final FileReferenceSet set;
		
		YamlTextFileReference(FileReferenceSet set, TextRange range, int index, String text){
			super(set, range, index, text);
			this.set = set;
		}
		
		protected PsiElement rename(String newName) throws IncorrectOperationException{
			TextRange range = getRangeInElement()
					.shiftLeft(getRangeInElement().getStartOffset() - 1)
					.grown(getRangeInElement().getStartOffset() - set.getStartInElement());
			PsiElement element = getElement();
			if(element instanceof YAMLKeyValue){
				YAMLKeyValue keyValue = (YAMLKeyValue)element;
				var value = keyValue.getValue();
				if(value instanceof YAMLScalar){
					try{
						var newText = value.getText().substring(0, range.getStartOffset()) + newName + value.getText().substring(range.getEndOffset());
						if(newText.equals("\"\"")) // change empty refs to /
							newText = "\"/\"";
						var generator = YAMLElementGenerator.getInstance(element.getProject());
						var dummyFile = generator.createDummyYamlWithText(newText);
						var newElement = PsiTreeUtil.collectElementsOfType(dummyFile, YAMLScalar.class).iterator().next();
						return value.replace(newElement);
					}catch(IncorrectOperationException e){
						LOG.error("Cannot rename " + getClass() + " from " + set.getClass() + " to " + newName, e);
						throw e;
					}
				}
				throw new IncorrectOperationException("Cannot rename value that is not a scalar");
			}
			throw new IncorrectOperationException("Cannot rename element that is not a key/value");
		}
	}
}