subprojects {
    dependencies {
        compileOnly(project(":api:spigot-api"))
    }
}

tasks.jar {
    enabled = false
}