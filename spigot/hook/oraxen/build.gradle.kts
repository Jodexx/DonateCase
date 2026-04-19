plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    maven("https://repo.oraxen.com/releases")
}

dependencies {
    compileOnly("io.th0rgal:oraxen:1.212.0")
}