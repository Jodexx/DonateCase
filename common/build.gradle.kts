plugins {
    `java-library`
}

group = "com.jodexindustries.donatecase"

repositories {
    mavenCentral()
}

dependencies {
    compileOnlyApi("com.google.code.gson:gson:2.13.2")
    compileOnlyApi("net.luckperms:api:5.5")
    compileOnlyApi(project(":api"))
    compileOnlyApi("com.github.retrooper:packetevents-api:2.13.0")
    compileOnlyApi("net.kyori:adventure-api:4.26.1")
}