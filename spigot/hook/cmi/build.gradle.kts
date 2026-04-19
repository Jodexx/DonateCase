plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    compileOnly("com.github.Zrips:CMI-API:9.8.6.4")
    compileOnly("com.github.Zrips:CMILib:1.5.8.1")
}