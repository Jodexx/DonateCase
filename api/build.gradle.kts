plugins {
    id("java")
    id("java-library")
    `maven-publish`
}

val apiVersion: String = properties["api"].toString()
group = "com.jodexindustries.donatecase.api"
version = apiVersion


dependencies {
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.yaml:snakeyaml:1.27")
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven {
            name = "JodexIndustries"
            url = uri("https://repo.jodexindustries.xyz/releases")
            credentials {
                username = findProperty("repoUser") as String?
                password = findProperty("repoPassword") as String?
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.jodexindustries.donatecase"
            artifactId = "api"
            version = apiVersion
            from(components["java"])
        }
    }
}