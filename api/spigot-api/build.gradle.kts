plugins {
    id("java")
    id("java-library")
    `maven-publish`
}

group = "com.jodexindustries.donatecase.api"
version = project(":api").version

dependencies {
    compileOnlyApi("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    api(project(":api"))
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.jodexindustries.donatecase"
            artifactId = "spigot-api"
            from(components["java"])
        }
    }
}

tasks.javadoc {
    val toolchain = project.extensions
        .getByType(JavaToolchainService::class.java)

    javadocTool.set(
        toolchain.javadocToolFor {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    )

    (options as StandardJavadocDocletOptions).apply {
        links(
            "https://docs.oracle.com/en/java/javase/22/docs/api/",
            "https://helpch.at/docs/1.16.5/",
            )
    }
    source = sourceSets["main"].allJava
}