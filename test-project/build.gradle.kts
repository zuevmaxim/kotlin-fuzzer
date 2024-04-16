plugins {
    kotlin("jvm") version "1.9.20"
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.2")

    // ktor
    implementation("io.ktor:ktor-server-netty:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    implementation("com.athaydes.rawhttp:rawhttp-core:2.2.2")

    // apache
    implementation("org.apache.httpcomponents:httpclient:4.5")
    implementation("org.apache.commons:commons-compress:1.20")

    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-mock:2.0.8")
    implementation("org.springframework:spring-test:4.3.0.RELEASE")

    // asm
    implementation("org.ow2.asm:asm:8.0.1")
}

tasks {
    compileKotlin {
//        kotlinOptions.jvmTarget = "11"
    }

    "jar"(org.gradle.jvm.tasks.Jar::class) {
        enabled = true
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { }
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        from(sourceSets.main.get().output)
    }
}
