buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "9.0.2"
}

group = "com.jodexindustries.donatecase"

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
    compileOnly("dev.lone:api-itemsadder:4.0.10")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.17")
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")
    compileOnly("de.oliver:FancyHolograms:2.4.0")
    compileOnly("com.github.retrooper:packetevents-spigot:2.9.4")
    compileOnly(fileTree("libs").include("*.jar"))
    implementation(project(":api:spigot-api"))
    implementation(project(":common"))
    implementation("dev.rollczi:liteskullapi:2.0.0")
}

tasks.runServer {
    minecraftVersion("1.21.10")
    allJvmArgs = listOf("-DPaper.IgnoreJavaVersion=true")
    javaLauncher = javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.jar {
    enabled = false
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    archiveBaseName.set(project.rootProject.name)
    archiveClassifier.set("")
    exclude("org/jetbrains/**")
    exclude("org/intellij/lang/**")

    relocate("dev.rollczi", "com.jodexindustries")

    minimize {
        include(dependency("dev.rollczi:.*"))
    }
}
