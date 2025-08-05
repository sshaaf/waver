// The java plugin, repositories, group, version, and toolchain are inherited

plugins {
    `java-library` // or 'java'
}

dependencies {
    // Common dependencies (langchain4j, junit) are inherited from the root

    // Specific dependencies for this module
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1")
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