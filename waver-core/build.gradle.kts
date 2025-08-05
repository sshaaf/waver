plugins {
    id("java")
}

group = "dev.shaaf.waver.core"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    // Configure the Java toolchain
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // Langchain core
    implementation("dev.langchain4j:langchain4j:1.0.0")

    // support gemini and OpenAI
    implementation("dev.langchain4j:langchain4j-open-ai:1.0.0")
    implementation("dev.langchain4j:langchain4j-google-ai-gemini:1.0.0-beta5")

    // Jackson for JSON and YAML processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1")

    // JGit
    implementation("org.eclipse.jgit:org.eclipse.jgit:7.3.0.202506031305-r")

    // Flexmark for Markdown processing and format conversion
    implementation("com.vladsch.flexmark:flexmark:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-util:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-ext-tables:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-ext-gfm-strikethrough:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-ext-autolink:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-html2md-converter:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-pdf-converter:0.64.8")
}

tasks.test {
    useJUnitPlatform()
}