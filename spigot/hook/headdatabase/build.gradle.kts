plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
}