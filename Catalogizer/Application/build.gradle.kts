plugins {

    id("buildsrc.convention.kotlin-jvm")

    application
}

dependencies {

    implementation(project(":Core"))
}

application {

    mainClass = "com.catalogizer.app.ApplicationKt"
}
