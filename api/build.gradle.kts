plugins {
    id("java")
    id("java-library")
}

val apiVersion: String = properties["api-version"].toString()
group = "com.jodexindustries.donatecase.api"
version = apiVersion


dependencies {
    compileOnly("org.jetbrains:annotations:24.1.0")
}