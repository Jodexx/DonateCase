plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    maven("https://repo.nexomc.com/releases") // Nexo
}

dependencies {
    compileOnly("com.nexomc:nexo:1.21.0")
}