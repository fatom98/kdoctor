plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.22-1.0.18")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.2")
}