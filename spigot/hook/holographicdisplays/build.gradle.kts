plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")
}