plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    maven("https://repo.fancyinnovations.com/releases")
}

dependencies {
    compileOnly("de.oliver:FancyHolograms:2.9.1")
}