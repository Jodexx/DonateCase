plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "DonateCase"
include(
    "common", "api",
    ":api:spigot-api", "velocity", "spigot", "spigot:jar",
    "spigot:hook:nexo", "spigot:hook:oraxen", "spigot:hook:customheads",
    "spigot:hook:cmi", "spigot:hook:fancyholograms", "spigot:hook:decentholograms",
    "spigot:hook:holographicdisplays", "spigot:hook:itemsadder", "spigot:hook:headdatabase"
)

file("addons").listFiles()?.forEach { dir ->
    if (dir.isDirectory) {
        include("addons:${dir.name}")
    }
}