plugins {
    // Apply the java plugin to add support for Java
    java

}

repositories {
    // Use Maven Central for resolving dependencies
    mavenCentral()
}

java {
    // Configure the Java toolchain
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<Jar> {
        destinationDirectory.set(file("${rootProject.buildDir}/libs"))
    }
}
