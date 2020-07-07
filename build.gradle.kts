import kotlinx.fuzzer.publish.mavenCentralMetadata
import kotlinx.fuzzer.publish.publishBintray
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    kotlin("jvm") version "1.3.72"

    application

    `maven-publish`
}

val mainClass = "kotlinx.fuzzer.cli.MainKt"

application {
    mainClassName = mainClass
}

repositories {
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(kotlin("reflect"))

    // code coverage
    implementation("org.jacoco:org.jacoco.core:0.8.5")
    implementation(project(":coverage"))

    // load classes && work with packages
    implementation("com.google.guava:guava:28.2-jre")
    implementation("net.bytebuddy:byte-buddy-agent:1.10.10")

    // command line arguments parser
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.2.1")

    // time format
    implementation("org.apache.commons:commons-lang3:3.0")


    // tests
    val junitVersion = "5.6.0"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testCompile("org.junit.jupiter:junit-jupiter-params:$junitVersion")
}

tasks {
    "test"(Test::class) {
        dependsOn("cleanTest")
        useJUnitPlatform()
        testLogging {
            events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        }
    }
    withType(KotlinCompile::class) {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xallow-result-return-type")
        }
    }

    task("fatJar", Jar::class) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes("Main-Class" to mainClass) }
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        from(sourceSets.main.get().output)
    }
}

val properties = Properties().apply {
    val file = File("local.properties")
    if (!file.exists()) return@apply
    load(file.inputStream())
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            mavenCentralMetadata()
        }
        mavenCentralMetadata()
        publishBintray(properties)
    }
}
