plugins {
    // Apply the application plugin to add support for building a CLI application in Java
    application

    id("org.graalvm.buildtools.native") version "0.10.2"
}

repositories {
    // Use Maven Central for resolving dependencies
    mavenCentral()
}

dependencies {
    // Add dependencies here if needed
    implementation(project(":waver-core"))
    // Langchain core
    implementation("dev.langchain4j:langchain4j:1.0.0")

    // support gemini and OpenAI
    implementation("dev.langchain4j:langchain4j-open-ai:1.0.0")
    implementation("dev.langchain4j:langchain4j-google-ai-gemini:1.0.0-beta5")

    // logging
    implementation("org.slf4j:slf4j-api:2.0.17")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")

    // Use JUnit Jupiter for testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // Cli libs
    implementation("info.picocli:picocli:4.7.7")
    annotationProcessor("info.picocli:picocli-codegen:4.7.7")
}

application {
    // Define the main class for the application
    mainClass.set("dev.shaaf.waver.cli.Main")
}

java {
    // Configure the Java toolchain
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

graalvmNative {
    // Disable metadata repository as we're providing explicit paths
    metadataRepository {
        enabled.set(false)
    }

    binaries {
        named("main") {
            imageName.set("waver")

            // Explicitly specify the paths to our configuration files
            configurationFileDirectories.from(file("src/main/resources/META-INF/native-image"))

            // Add build arguments for native image compilation
            buildArgs.addAll(
                "--initialize-at-build-time=dev.shaaf.waver",
                "--initialize-at-build-time=dev.langchain4j",
                "--initialize-at-build-time=com.fasterxml.jackson",
                "--initialize-at-build-time=org.slf4j",
                "--initialize-at-build-time=org.yaml.snakeyaml.util.UriEncoder",
                "--initialize-at-build-time=org.yaml.snakeyaml.events.ImplicitTuple",
                "--initialize-at-build-time=org.yaml.snakeyaml.external.com.google.gdata.util.common.base.UnicodeEscaper",
                "--initialize-at-build-time=org.yaml.snakeyaml.DumperOptions\$ScalarStyle",
                "--initialize-at-build-time=org.yaml.snakeyaml.nodes.Tag",
                "--initialize-at-build-time=org.yaml.snakeyaml.external.com.google.gdata.util.common.base.PercentEscaper",
                "--initialize-at-run-time=io.netty",
                "--initialize-at-run-time=dev.langchain4j.internal.RetryUtils",
                "--no-fallback",
                "--enable-https",
                "--enable-url-protocols=https",
                "-H:+ReportExceptionStackTraces",
                "-H:+UnlockExperimentalVMOptions"
            )
        }
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests
    useJUnitPlatform()
}

// Create a fat JAR with all dependencies
tasks.register<Jar>("fatJar") {
    description = "Create a fat JAR with all dependencies"
    group = "build"

    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "dev.shaaf.waver.cli.Main"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

// Task to generate shell completion script
tasks.register<JavaExec>("generateCompletion") {
    description = "Generate shell completion script for Waver"
    group = "build"

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("picocli.AutoComplete")

    args = listOf(
        "--force", // overwrite existing script
        "--completionScript", // generate completion script
        "waver-completion.sh", // output file name
        "dev.shaaf.waver.cli.Main" // main class name
    )
}