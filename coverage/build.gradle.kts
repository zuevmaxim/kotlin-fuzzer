import kotlinx.fuzzer.publish.mavenCentralMetadata
import kotlinx.fuzzer.publish.publishBintray
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.*
import java.util.*


plugins {
    kotlin("jvm") version "1.9.20"
    java
    application
    `maven-publish`
}

repositories {
    mavenCentral()

}

dependencies {
    val asmVersion = "9.1"
    implementation("org.ow2.asm:asm:$asmVersion")
    implementation("org.ow2.asm:asm-commons:$asmVersion")
    implementation("org.ow2.asm:asm-util:$asmVersion")

    implementation("org.jetbrains:annotations:20.0.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            mavenCentralMetadata()
        }
        mavenCentralMetadata()
    }
}
