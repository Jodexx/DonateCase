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
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(8)
        options.compilerArgs.add("-Xlint:-deprecation")
        options.compilerArgs.add("-Xlint:-options")
        options.compilerArgs.add("-Xdoclint:none")
    }
}