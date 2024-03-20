plugins {
    id("com.google.devtools.ksp") version "1.9.22-1.0.18"
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