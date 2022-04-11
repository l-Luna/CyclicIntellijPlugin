package cyclic.intellij.projects;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.*;

import java.util.Set;

import static cyclic.intellij.projects.CyclicProjectYamlFilePatcher.PROJECT_YAML_EXTENSION;

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
			// use the text from the value to create references, but attach them to the entry
			return new FileReferenceSet(
					value.getTextValue(),
					entry,
					value.getStartOffsetInParent() + 1,
					this,
					true,
					false).getAllReferences();
		}
		
		@Nullable
		private static PsiFileSystemItem resolveBy(PsiDirectory directory, String[] pathElements){
			PsiFileSystemItem currentElement = directory;
			for(String pathElement : pathElements){
				if(currentElement == null)
					return null;
				if(pathElement.isEmpty() || pathElement.equals("."))
					continue;
				if(pathElement.equals("..")){
					currentElement = currentElement.getParent();
					continue;
				}
				if(currentElement instanceof PsiDirectory){
					if(pathElement.contains("."))
						currentElement = ((PsiDirectory)currentElement).findFile(pathElement);
					else
						currentElement = ((PsiDirectory)currentElement).findSubdirectory(pathElement);
				}else
					return null;
			}
			return currentElement;
		}
	}
}