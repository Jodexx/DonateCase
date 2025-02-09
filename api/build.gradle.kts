plugins {
    id("java")
    id("java-library")
    `maven-publish`
}

group = "com.jodexindustries.donatecase.api"
version = properties["api"].toString()


dependencies {
    api("net.kyori:event-method:3.0.0") {
        exclude("com.google.guava", "guava")
        exclude("org.checkerframework", "checker-qual")
    }
    compileOnlyApi("com.j256.ormlite:ormlite-jdbc:6.1")
    compileOnlyApi("org.jetbrains:annotations:24.1.0")
    compileOnlyApi("com.google.guava:guava:33.3.1-jre")
    compileOnlyApi("org.projectlombok:lombok:1.18.36")
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
            name = "JodexIndustries"
            url = uri("https://repo.jodexindustries.xyz/releases")
            credentials {
                username = findProperty("repoUser") as String?
                password = findProperty("repoPassword") as String?
            }
            authentication {
                create<BasicAuthentication>("basic")
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
            "https://docs.oracle.com/en/java/javase/22/docs/api/",
         )
    }
    source = sourceSets["main"].allJava
}

tasks.register<Delete>("cleanGenerated") {
   delete("${buildDir}/generated")
}

tasks.register<Copy>("generateJava") {
    from(project.file("src/template/java"))
    into("${buildDir}/generated/java")
    expand(properties)
}

sourceSets.main { java.srcDir("${buildDir}/generated/java") }

tasks.compileJava { dependsOn("generateJava", "cleanGenerated") }

tasks.named<Jar>("sourcesJar") { dependsOn("generateJava") }

sourceSets.main { resources.srcDir("src/generated/resources") }

