rootProject.name = "kdoctor"

plugins {
    kotlin("jvm") version "1.9.22" apply false
}

include("processor")
include("test")