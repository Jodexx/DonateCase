rootProject.name = "DonateCase"
include("api")
include("spigot")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.fancyplugins.de/releases")
        maven("https://repo.jodexindustries.xyz/releases/")
    }
}
