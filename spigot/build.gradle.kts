buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "9.0.0-rc2"
}

group = "com.jodexindustries.donatecase"

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.11")
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")
    compileOnly("de.oliver:FancyHolograms:2.3.3")
    compileOnly("com.github.retrooper:packetevents-spigot:2.9.3")
    compileOnly(fileTree("libs").include("*.jar"))
    implementation(project(":api:spigot-api"))
    implementation(project(":common"))

}

tasks.runServer {
    minecraftVersion("1.21.8")
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
    archiveVersion.set(project.version.toString())
    exclude("org/jetbrains/**")
    exclude("org/intellij/lang/**")
}
