plugins {
    id("java")
}

group = "com.jodexindustries.donatecase"
version = properties["version"].toString()

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.j256.ormlite:ormlite-jdbc:6.1")
    compileOnly(project(":api"))
}