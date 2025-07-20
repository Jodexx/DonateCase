buildscript {
    repositories {
        mavenCentral()
    }
}
plugins {
    java
}

group = "com.jodexindustries.dcblockanimations"
version = "2.0.2"

dependencies {
    compileOnly(project(":api:spigot-api"))
    compileOnly(project(":common"))
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("addon.yml") {
        expand(props)
    }
}