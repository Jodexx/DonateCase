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
    compileOnlyApi("com.google.code.gson:gson:2.11.0")
    compileOnlyApi("net.luckperms:api:5.4")
    compileOnlyApi(project(":api"))
}