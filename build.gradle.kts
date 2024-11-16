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

val apiVersion: String = properties["api"].toString()

tasks.register("replaceVersionInREADME") {
    doLast {
        ant.withGroovyBuilder {
            // Replace version in XML-like tags
            "replaceregexp"("match" to "<version>(listOf(0-9\\.)+)</version>", "replace" to "<version>$apiVersion</version>", "flags" to "g", "byline" to true) {
                "fileset"("dir" to ".", "includes" to "README.md")
            }
            // Replace version in Gradle-like assignment
            "replaceregexp"("match" to "com\\.jodexindustries\\.donatecase\\ =DonateCaseAPI\\ =(listOf(0-9\\.)+)", "replace" to "com.jodexindustries.donatecase =DonateCaseAPI =$apiVersion", "flags" to "g", "byline" to true) {
                "fileset"("dir" to ".", "includes" to "README.md")
            }
        }
    }
}