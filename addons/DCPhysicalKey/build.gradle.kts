buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
}

group = "com.jodexindustries.dcphysicalkey"
version = "2.0.1"

dependencies {
    compileOnly(project(":spigot"))
    compileOnly(project(":common"))
    compileOnly(project(":api:spigot-api"))
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("addon.yml") {
        expand(props)
    }
}