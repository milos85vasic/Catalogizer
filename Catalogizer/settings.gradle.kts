dependencyResolutionManagement {


    @Suppress("UnstableApiUsage")
    repositories {

        mavenCentral()
    }
}

plugins {

    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(":Application")
include(":Core")

rootProject.name = "Catalogizer"