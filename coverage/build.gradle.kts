plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    val asmVersion = "8.0.1"
    implementation("org.ow2.asm:asm:$asmVersion")
    implementation("org.ow2.asm:asm-commons:$asmVersion")
    implementation("org.ow2.asm:asm-util:$asmVersion")

    implementation("org.jetbrains:annotations:20.0.0")
}
