plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.9.9")
}