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
    compileOnly("com.j256.ormlite:ormlite-jdbc:6.1")
    compileOnly("com.google.code.gson:gson:2.11.0")
    compileOnlyApi("net.luckperms:api:5.4")
    compileOnly(project(":api"))
}