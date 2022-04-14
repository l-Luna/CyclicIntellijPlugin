plugins{
	id("org.jetbrains.intellij") version "1.5.2"
	// TODO: Use the plugin to build the parser automatically - requires splitting parser and lexer
	//id("antlr")
	java
}

group = "com.github.l-Luna.CyclicIntellijPlugin"
version = "0.0.3"

repositories{
	mavenCentral()
}

dependencies{
	implementation("org.antlr:antlr4-runtime:4.9.3")
	implementation("org.antlr:antlr4-intellij-adaptor:0.1")
	//implementation("org.antlr:antlr4-master:4.9.3")
	
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij{
	version.set("2022.1")
	plugins.add("com.intellij.java")
	plugins.add("yaml")
	plugins.add("coverage")
}

tasks{
	patchPluginXml{
		changeNotes.set(
			"""
            First version.
			""".trimIndent()
		)
	}
}

tasks.getByName<Test>("test"){
	useJUnitPlatform()
}