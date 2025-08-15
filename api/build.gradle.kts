plugins {
    `java-library`
    `maven-publish`
}

group = "com.jodexindustries.donatecase.api"
version = properties["api"]!!


dependencies {
    compileOnlyApi("net.kyori:event-method:3.0.0")
    compileOnlyApi("com.j256.ormlite:ormlite-jdbc:6.1")
    compileOnlyApi("org.jetbrains:annotations:24.0.0")
    compileOnlyApi("com.google.guava:guava:33.3.1-jre")
    compileOnlyApi("org.spongepowered:configurate-yaml:4.1.2")
}


java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

publishing {
    repositories {
        maven {
            name = "Jodex"
            url = uri("https://repo.jodex.xyz/releases")
            credentials {
                username = findProperty("jodexRepoUser") as String? ?: System.getenv("JODEX_REPO_USER")
                password = findProperty("jodexRepoPassword") as String? ?: System.getenv("JODEX_REPO_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.jodexindustries.donatecase"
            artifactId = "api"
            from(components["java"])
        }
    }
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).apply {
        links(
            "https://docs.oracle.com/en/java/javase/22/docs/api/"
        )
    }
}

val generatedDir = layout.buildDirectory.dir("generated/java")

tasks.register<Delete>("cleanGenerated") {
    delete(generatedDir)
}

tasks.register<Copy>("generateJava") {
    from("src/template/java")
    into(generatedDir)
    expand(project.properties)
    filteringCharset = "UTF-8"
}

sourceSets {
    main {
        java.srcDir(generatedDir)
        resources.srcDir("src/generated/resources")
    }
}

tasks.compileJava {
    dependsOn("cleanGenerated", "generateJava")
}

tasks.named<Jar>("sourcesJar") {
    dependsOn("generateJava")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
