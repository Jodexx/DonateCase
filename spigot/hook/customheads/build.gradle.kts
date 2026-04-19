plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    compileOnly(fileTree("libs"))
}