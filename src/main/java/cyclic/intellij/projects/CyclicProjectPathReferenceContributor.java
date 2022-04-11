package cyclic.intellij.projects;

import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLElementGenerator;
import org.jetbrains.yaml.psi.*;
import org.jetbrains.yaml.psi.impl.YAMLQuotedTextImpl;

import java.util.Objects;
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
						if(topMapping.getParent() instanceof YAMLDocument){
							YAMLDocument document = (YAMLDocument)topMapping.getParent();
							if(document.getContainingFile() != null){
								PsiFile file = document.getContainingFile();
								return new PsiReference[]{new CyclicProjectPathReference(value, entry, file)};
							}
						}
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
											if(topMapping.getParent() instanceof YAMLDocument){
												var document = (YAMLDocument)topMapping.getParent();
												if(document.getContainingFile() != null){
													PsiFile file = document.getContainingFile();
													return new PsiReference[]{new CyclicProjectPathReference(value, entry, file)};
												}
											}
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
		
		private static class CyclicProjectPathReference implements PsiReference{
			
			private final YAMLScalar value;
			private final YAMLKeyValue source;
			private final PsiFile file;
			
			public CyclicProjectPathReference(YAMLScalar value, YAMLKeyValue source, PsiFile file){
				this.value = value;
				this.source = source;
				this.file = file;
			}
			
			public @NotNull PsiElement getElement(){
				return source;
			}
			
			public @NotNull TextRange getRangeInElement(){
				return value.getTextRangeInParent().grown(-2).shiftRight(1);
			}
			
			public @Nullable PsiElement resolve(){
				PsiDirectory directory = file.getParent();
				if(directory != null){
					// not necessarily inside the directory
					String path = value.getText();
					// remove quotes
					path = path.substring(1, path.length() - 1);
					// TODO: do backslashes need to be escaped in YAML?
					String[] pathElements = path.split("[/\\\\]");
					return resolveBy(directory, pathElements);
				}
				return null;
			}
			
			public @NotNull @NlsSafe String getCanonicalText(){
				return value.getText();
			}
			
			public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException{
				// we need to only replace the last part of our path
				var path = value.getText();
				path = path.substring(1, path.length() - 1);
				var pathElements = path.split("[/\\\\]");
				// join all elements except last
				var newPath = StringUtil.join(pathElements, 0, pathElements.length - 1, "/")
						    + "/" + newElementName;
				if(!newElementName.contains("."))
					newPath += "/";
				// generate an appropriate YAML string
				var generator = YAMLElementGenerator.getInstance(source.getProject());
				var dummyFile = generator.createDummyYamlWithText("\"" + newPath + "\"");
				var dummyText = PsiTreeUtil.collectElementsOfType(dummyFile, YAMLQuotedTextImpl.class)
						.iterator().next();
				value.replace(dummyText);
				return source;
			}
			
			public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException{
				if(element instanceof PsiNamedElement && ((PsiNamedElement)element).getName() != null)
					return handleElementRename(((PsiNamedElement)element).getName());
				throw new IncorrectOperationException("Cannot bind to unnamed element " + element);
			}
			
			public boolean isReferenceTo(@NotNull PsiElement element){
				return Objects.equals(resolve(), element);
			}
			
			public boolean isSoft(){
				return false;
			}
			
			/*public Object @NotNull [] getVariants(){
				// get a folder using resolveBy, excluding the last element
				var path = value.getText();
				path = path.substring(1, path.length() - 1);
				var pathElements = path.split("[/\\\\]");
				// remove last element from pathElements
				var newPathElements = new String[pathElements.length - 1];
				System.arraycopy(pathElements, 0, newPathElements, 0, newPathElements.length);
				var folder = resolveBy(file.getParent(), newPathElements);
				if(folder instanceof PsiDirectory){
					var result = new ArrayList<LookupElement>();
					for(var subDir : ((PsiDirectory)folder).getSubdirectories())
						result.add(LookupElementBuilder.create(subDir.getName()).withIcon(subDir.getIcon(0)));
					for(var file : ((PsiDirectory)folder).getFiles())
						result.add(LookupElementBuilder.create(file.getName()).withIcon(file.getIcon(0)));
					return result.toArray();
				}
				return new Object[0];
			}*/
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