rootProject.name = "DonateCase"
include("common", "api", ":api:spigot-api",
    "spigot")

file("addons").listFiles()?.forEach { dir ->
    if (dir.isDirectory) {
        include("addons:${dir.name}")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.fancyplugins.de/releases")
        maven("https://repo.jodexindustries.xyz/releases/")
        maven("https://repo.alessiodp.com/releases/")
    }
}
