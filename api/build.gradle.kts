plugins {
    id("java")
    id("java-library")
    `maven-publish`
}

group = "com.jodexindustries.donatecase.api"
version = properties["api"].toString()


dependencies {
    compileOnlyApi("net.kyori:event-api:3.0.0")
    api("net.kyori:event-method:3.0.0") {
        exclude("com.google.guava", "guava")
        exclude("org.checkerframework", "checker-qual")
    }
    compileOnlyApi("com.j256.ormlite:ormlite-jdbc:6.1")
    compileOnlyApi("org.jetbrains:annotations:24.0.0")
    compileOnlyApi("com.google.guava:guava:33.3.1-jre")
    compileOnlyApi("org.projectlombok:lombok:1.18.36")
    compileOnlyApi("org.spongepowered:configurate-yaml:4.1.2")

    annotationProcessor("org.projectlombok:lombok:1.18.36")
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

val delombokClasspath: Configuration by configurations.creating {
    extendsFrom(configurations.compileOnly.get())
    isCanBeResolved = true
}


val delombok by tasks.registering(JavaExec::class) {
    group = "build"
    description = "Delombok generated sources to ensure Javadocs are generated correctly."
    mainClass.set("lombok.launch.Main")

    classpath = delombokClasspath
    args = listOf("delombok", "src/main/java", "-d", "${buildDir}/generated/sources/delombok")
}

tasks.javadoc {
    dependsOn(delombok)
    source = fileTree("${buildDir}/generated/sources/delombok")
    (options as StandardJavadocDocletOptions).apply {
        links(
            "https://docs.oracle.com/en/java/javase/22/docs/api/"
        )
    }
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(delombok)
    from("${buildDir}/generated/sources/delombok")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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

