plugins {
    // The java plugin is applied automatically from the root script
    id("io.quarkus")
}

dependencies {
    // The Quarkus BOM provides versions for Quarkus artifacts
    implementation(enforcedPlatform("${project.property("quarkusPlatformGroupId")}:${project.property("quarkusPlatformArtifactId")}:${project.property("quarkusPlatformVersion")}"))

    // Common dependencies (langchain4j, slf4j) are inherited from the root
    implementation(project(":core"))

    // Specific dependencies for this module
    implementation("io.minio:minio:8.5.17")
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-funqy-http") // The duplicate line was removed
    implementation("io.quarkus:quarkus-funqy-knative-events-deployment:3.23.0")

    // Test dependencies
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.eclipse.jgit:org.eclipse.jgit:6.10.0.202406032230-r")
    testImplementation("org.awaitility:awaitility:4.2.1")
}