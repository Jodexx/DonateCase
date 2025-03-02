buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
}

group = "com.jodexindustries"
version = "2.0.0"

dependencies {
    compileOnly("com.google.code.gson:gson:2.12.1")
    compileOnly(project(":api"))
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("addon.yml") {
        expand(props)
    }
}