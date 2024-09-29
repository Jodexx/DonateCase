buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.rikonardo.papermake") version "1.0.6"
}

group = "com.jodexindustries.donatecase"
version = properties["version"].toString()
val api: String = properties["api"].toString()

dependencies {
    compileOnly(project(":api"))
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
    compileOnly("de.likewhat.customheads:CustomHeads:3.0.7")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.11")
    compileOnly(fileTree("libs").include("*.jar"))
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")
    compileOnly("de.oliver:FancyHolograms:2.3.3")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.j256.ormlite:ormlite-jdbc:6.1")
    compileOnly("com.github.retrooper:packetevents-spigot:2.4.0")
    compileOnly("me.tofaa.entitylib:spigot:2.4.10-SNAPSHOT")
//    compileOnly("io.th0rgal:oraxen:1.180.1")
    implementation("com.alessiodp.libby:libby-bukkit:2.0.0-SNAPSHOT")
}

tasks.build {
    dependsOn("shadowJar")
}

java {
    withSourcesJar()
//    withJavadocJar()
}

tasks.jar {
    enabled = false
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    archiveBaseName.set(project.name)
    archiveClassifier.set(null as String?)
    archiveVersion.set(project.version.toString())
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
            artifactId = "DonateCaseAPI"
            version = api
            from(components["java"])
        }
    }
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).apply {
        links(
            "https://docs.oracle.com/en/java/javase/22/docs/api/",
            "https://helpch.at/docs/1.16.5/",
            "https://javadoc.io/static/net.luckperms/api/5.4/",
            "https://milkbowl.github.io/VaultAPI/"
        )
    }
    source = sourceSets["main"].allJava
}