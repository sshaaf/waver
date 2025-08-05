// waver/build.gradle.kts

//
// 1. PLUGIN MANAGEMENT
//
// Define versions for all plugins used across the project in one place.
// 'apply false' makes the plugin available to sub-projects without applying it to the root.
plugins {
    java
    application

    id("io.quarkus") version "3.24.5" apply false // Assuming a recent Quarkus version
    id("org.graalvm.buildtools.native") version "0.10.2" apply false
}


//
// 2. CONFIGURATION FOR ALL MODULES (ROOT + SUB-PROJECTS)
//
allprojects {
    group = "dev.shaaf.waver"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal() // Include all repositories needed by any sub-project
    }
}


//
// 3. CONFIGURATION FOR SUB-PROJECTS ONLY
//
subprojects {
    // Apply the Java plugin to all sub-projects by default
    apply(plugin = "java")

    // Configure the Java toolchain for all sub-projects
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    //
    // 4. CENTRALIZED DEPENDENCY MANAGEMENT
    //
    dependencies {
        // Define common dependencies for all or most modules here.
        // This avoids repeating them in each sub-project's build file.

        // Langchain dependencies used by all modules
        implementation("dev.langchain4j:langchain4j:1.0.0")
        implementation("dev.langchain4j:langchain4j-open-ai:1.0.0")
        implementation("dev.langchain4j:langchain4j-google-ai-gemini:1.0.0-beta5")

        // SLF4J for logging (used by cli and backend)
        implementation("org.slf4j:slf4j-api:2.0.17")
        runtimeOnly("org.slf4j:slf4j-simple:2.0.17")

        // Use a JUnit BOM (Bill of Materials) for consistent test dependency versions
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    // Ensure all tests use JUnit Platform
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}