plugins {
    java
    eclipse
    idea
    id("xyz.jpenilla.run-velocity") version "2.3.1"
    id("com.gradleup.shadow") version "9.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    implementation("org.xerial:sqlite-jdbc:3.46.1.0")
    implementation("com.mysql:mysql-connector-j:8.4.0")
    implementation("org.postgresql:postgresql:42.7.8")

    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
  runVelocity {
    velocityVersion("3.4.0-SNAPSHOT")
  }
  test {
    useJUnitPlatform()
  }
}

tasks.withType<JavaCompile> {
    options.release.set(17)
}

var templateSource = file("src/main/templates")
var templateDest = layout.buildDirectory.dir("generated/sources/templates")!!
var generateTemplates = tasks.register<Copy>("generateTemplates") {
    var props = mapOf("version" to project.version)
    inputs.properties(props)

    from(templateSource)
    into(templateDest)
    expand(props)
}

the<SourceSetContainer>().named("main") {
    java.srcDir(templateDest)
}

tasks.named("compileJava") {
    dependsOn(generateTemplates)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.shadowJar {
    archiveBaseName.set("${project.rootProject.name}-velocity")
    archiveClassifier.set("")
}
