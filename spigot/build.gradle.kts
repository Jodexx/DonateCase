buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.rikonardo.papermake") version "1.0.6"
}

group = "com.jodexindustries.donatecase"
version = properties["version"].toString()

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
    compileOnly("de.likewhat.customheads:CustomHeads:3.0.7")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.11")
    compileOnly(fileTree("libs").include("*.jar"))
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")
    compileOnly("de.oliver:FancyHolograms:2.3.3")
    compileOnly("com.github.retrooper:packetevents-spigot:2.6.0")
    implementation("me.tofaa.entitylib:spigot:2.4.11-SNAPSHOT")
    implementation(project(":api:spigot-api"))
    implementation(project(":common"))

}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
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
    archiveClassifier.set(null as String?)
    archiveVersion.set(project.version.toString())
    dependencies {
        exclude(dependency("org.jetbrains:annotations:.*"))
    }

    relocate("net.kyori.event", "com.jodexindustries.donatecase.api.event")
    relocate("me.tofaa.entitylib", "com.jodexindustries.donatecase.entitylib")
}
