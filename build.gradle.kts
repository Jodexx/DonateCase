buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
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
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-releases/")
        maven("https://repo.fancyplugins.de/releases")
        maven("https://repo.alessiodp.com/releases/")
        maven("https://jitpack.io")
        maven("https://repo.jodex.xyz/releases/")
    }

    dependencies {
        annotationProcessor("org.projectlombok:lombok:1.18.36")
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(8)
        options.compilerArgs.add("-Xlint:-deprecation")
        options.compilerArgs.add("-Xlint:-options")
        options.compilerArgs.add("-Xdoclint:none")
    }
}