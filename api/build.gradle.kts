plugins {
    id("java")
    id("java-library")
    `maven-publish`
}

group = "com.jodexindustries.donatecase.api"
version = properties["api"].toString()


dependencies {
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.yaml:snakeyaml:1.27")
    compileOnly("com.google.guava:guava:33.3.1-jre")
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

