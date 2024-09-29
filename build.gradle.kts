buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
}

defaultTasks("clean", "build")

subprojects {
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(8)
    }
}

tasks.jar {
    enabled = false
}

val apiVersion: String = properties["api-version"].toString()

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
