plugins {
    application
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
}

group = "org.meatball.lunch"
version = "1.0-SNAPSHOT"

application {
    mainClass = "org.meatball.lunch.SlackMainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.slack.api:bolt:1.36.1")
    implementation("com.slack.api:bolt-servlet:1.36.1")
    implementation("com.slack.api:bolt-jetty:1.36.1")
    implementation("com.slack.api:slack-api-model-kotlin-extension:1.36.1")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation(project(":lunch-core"))
}

tasks.withType<Jar> {
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    manifest {
        attributes["Main-Class"] = "org.meatball.lunch.SlackMainKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
}


tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}