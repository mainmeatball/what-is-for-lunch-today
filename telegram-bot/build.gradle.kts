plugins {
    id("myproject.java-conventions")
    application
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
}

application {
    mainClass = "org.meatball.lunch.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.telegram:telegrambots:6.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation(project(":lunch-core"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "org.meatball.lunch.MainKt"
    }

    duplicatesStrategy = DuplicatesStrategy.FAIL

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}


tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}