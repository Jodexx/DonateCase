plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    maven("https://repo.loohpjames.com/repository/")
}

dependencies {
    compileOnly("dev.lone:api-itemsadder:4.0.10")
}