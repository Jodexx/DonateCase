plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "DonateCase"
include("common", "api", ":api:spigot-api",
    "spigot")

file("addons").listFiles()?.forEach { dir ->
    if (dir.isDirectory) {
        include("addons:${dir.name}")
    }
}