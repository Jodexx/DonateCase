buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    id("com.gradleup.shadow")
}

group = "com.jodexindustries.donatecase"

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.7")
    compileOnly("com.github.retrooper:packetevents-spigot:2.11.1")
    implementation(project(":api:spigot-api"))
    implementation(project(":common"))
    implementation("com.github.Jodexx:LiteSkullAPI:2.0.0")
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
    archiveBaseName.set(project.name)
    archiveClassifier.set("")
    exclude("org/jetbrains/**")
    exclude("org/intellij/lang/**")

    relocate("dev.rollczi", "com.jodexindustries")

    minimize {
        include(dependency("dev.rollczi:.*"))
    }
}
