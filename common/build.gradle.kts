plugins {
    id("java")
    id("java-library")
}

group = "com.jodexindustries.donatecase"
version = properties["version"].toString()

repositories {
    mavenCentral()
}

dependencies {
    api("net.kyori:event-method:3.0.0") {
        exclude("com.google.guava", "guava")
        exclude("org.checkerframework", "checker-qual")
    }
    compileOnlyApi("com.google.code.gson:gson:2.11.0")
    compileOnlyApi("net.luckperms:api:5.4")
    compileOnly(project(":api"))
}