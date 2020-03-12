import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.70"

    application
}

application {
    mainClassName = "ru.example.kotlinfuzzer.MainKt"
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(kotlin("reflect"))

    // code coverage
    implementation("org.jacoco:org.jacoco.core:0.8.5")

    // tests
    val junitVersion = "5.6.0"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testCompile("org.junit.jupiter:junit-jupiter-params:$junitVersion")
}

tasks {
    "test"(Test::class) {
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
}
