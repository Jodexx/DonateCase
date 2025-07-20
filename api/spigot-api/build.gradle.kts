plugins {
    id("java")
    id("java-library")
    `maven-publish`
}

group = "com.jodexindustries.donatecase.api"
version = project(":api").version

dependencies {
    compileOnlyApi("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    api(project(":api"))
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

publishing {
    repositories {
        maven {
            name = "Jodex"
            url = uri("https://repo.jodex.xyz/releases")
            credentials {
                username = findProperty("jodexRepoUser") as String? ?: System.getenv("JODEX_REPO_USER")
                password = findProperty("jodexRepoPassword") as String? ?: System.getenv("JODEX_REPO_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.jodexindustries.donatecase"
            artifactId = "spigot-api"
            from(components["java"])
        }
    }
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).apply {
        links(
            "https://docs.oracle.com/en/java/javase/22/docs/api/",
            "https://repo.jodex.xyz/javadoc/releases/com/jodexindustries/donatecase/api/$version/raw/",
            "https://helpch.at/docs/1.16.5/",
            )
    }
    source = sourceSets["main"].allJava
}