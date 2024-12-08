buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
}

group = "com.jodexindustries"
version = "1.1.2"

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly(project(":api:spigot-api"))
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching(listOf("plugin.yml", "addon.yml")) {
        expand(props)
    }
}