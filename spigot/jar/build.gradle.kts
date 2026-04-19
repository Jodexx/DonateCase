import org.gradle.jvm.tasks.Jar

plugins {
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

val platformPaths = setOf(
    ":spigot",
    "spigot:hook:nexo", "spigot:hook:oraxen", "spigot:hook:customheads",
    "spigot:hook:cmi", "spigot:hook:fancyholograms", "spigot:hook:decentholograms",
    "spigot:hook:holographicdisplays", "spigot:hook:itemsadder", "spigot:hook:headdatabase"
)
val loadedProjects: List<Project> = platformPaths.map { rootProject.project(it) }

tasks.jar {
    enabled = false
}

tasks {
    shadowJar {
        archiveFileName.set("DonateCase-${project.version}.jar")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        fun registerProject(project: Project, jarTask: AbstractArchiveTask) {
            dependsOn(jarTask)
            dependsOn(project.tasks.withType<Jar>())
            from(zipTree(jarTask.archiveFile))
        }

        loadedProjects.forEach { p ->
            val task = p.tasks.findByName("shadowJar") as? AbstractArchiveTask
                ?: p.tasks.getByName("jar") as AbstractArchiveTask

            registerProject(p, task)
        }
    }
    build.get().dependsOn(shadowJar)
}

tasks.runServer {
    minecraftVersion("1.21.11")
    allJvmArgs = listOf("-DPaper.IgnoreJavaVersion=true")
    javaLauncher = javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}