import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.70"

    application
}

val mainClass = "ru.example.kotlinfuzzer.cli.MainKt"

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

    // guava - load classes && work with packages
    implementation("com.google.guava:guava:28.2-jre")

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
    "compileKotlin"(KotlinCompile::class) {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    "jar"(Jar::class) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes("Main-Class" to mainClass) }
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        from(sourceSets.main.get().output)
    }
}
