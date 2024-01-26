plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.21-1.0.16")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.2")
}