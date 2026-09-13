buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    id("com.gradleup.shadow") version "9.6.1" apply false
}

defaultTasks("clean", "build")

tasks.jar {
    enabled = false
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

subprojects {
    plugins.apply("java")

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-releases/")
        maven("https://repo.alessiodp.com/releases/")
        maven("https://jitpack.io")
        maven("https://repo.codemc.io/repository/maven-releases/") // packetevents
        maven("https://repo.extendedclip.com/releases/") // placeholderapi
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.48")
        annotationProcessor("org.projectlombok:lombok:1.18.48")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.compilerArgs.add("-Xlint:-deprecation")
        options.compilerArgs.add("-Xlint:-options")
        options.compilerArgs.add("-Xdoclint:none")
    }
}