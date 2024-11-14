plugins {
    id("java")
    id("java-library")
}

group = "com.jodexindustries.donatecase.api"
version = project(":api").version

dependencies {
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    implementation(project(":api"))
}