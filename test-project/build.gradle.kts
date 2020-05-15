plugins {
    kotlin("jvm") version "1.3.61"
    id("org.springframework.boot") version "2.2.7.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // ktor
    implementation("io.ktor:ktor-server-netty:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    implementation("com.athaydes.rawhttp:rawhttp-core:2.2.2")

    // apache
    implementation("org.apache.httpcomponents:httpclient:4.5")

    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-mock:2.0.8")
    implementation("org.springframework:spring-test:4.3.0.RELEASE")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
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
