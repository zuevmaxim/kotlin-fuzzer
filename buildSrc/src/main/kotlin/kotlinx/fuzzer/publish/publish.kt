package kotlinx.fuzzer.publish

import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.withType
import java.net.URI
import java.util.*

fun PublishingExtension.mavenCentralMetadata() {
    publications.withType(MavenPublication::class) {
        pom {
            if (!name.isPresent) {
                name.set(artifactId)
            }
            description.set("Kotlin fuzzer")
            url.set("https://github.com/zuevmaxim/kotlin-fuzzer")
        }
    }
}

fun PublishingExtension.publishBintray(properties: Properties) {

    fun getProperty(name: String) = properties.getProperty(name) ?: System.getenv(name)

    repositories {
        maven {
            url = URI("https://api.bintray.com/maven/zuevmaxim/com.github.zuevmaxim/kotlin-fuzzer/;publish=0")
            credentials {
                username = getProperty("BINTRAY_USER")
                password = getProperty("BINTRAY_API_KEY")
            }
        }
    }
}
