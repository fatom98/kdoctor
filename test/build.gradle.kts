plugins {
    id("com.google.devtools.ksp") version "1.9.21-1.0.16"
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    ksp(project(":processor"))
}

ksp {
    arg("packageToScan", "api")
}