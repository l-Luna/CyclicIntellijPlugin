plugins{
	id("org.jetbrains.intellij") version "1.8.0"
	antlr
	java
}

group = "com.github.l-Luna.CyclicIntellijPlugin"
version = "0.0.3"

repositories{
	mavenCentral()
}

dependencies{
	implementation("org.antlr:antlr4-runtime:4.10.1")
	implementation("org.antlr:antlr4-intellij-adaptor:0.1")
	
	antlr("org.antlr:antlr4:4.10.1")
	
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij{
	version.set("IC-2022.2.3")
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