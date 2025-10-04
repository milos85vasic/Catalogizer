plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    // Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
    implementation(libs.bundles.kotlinxEcosystem)

    // Project dependencies
    implementation(project(":Core"))
    implementation(project(":Samba"))

    // SQLite for database
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")

    // Database connection pooling
    implementation("com.zaxxer:HikariCP:5.1.0")

    // File type detection and metadata extraction
    implementation("org.apache.tika:tika-core:2.9.1")
    implementation("org.apache.tika:tika-parsers-standard-package:2.9.1")

    // Hashing and cryptography
    implementation("commons-codec:commons-codec:1.16.0")
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")

    // File watching and monitoring
    implementation("io.methvin:directory-watcher:0.18.0")

    // Virtual file system
    implementation("org.apache.commons:commons-vfs2:2.9.0")

    // Async processing and reactive streams
    implementation("io.projectreactor:reactor-core:3.6.0")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")

    // Performance monitoring
    implementation("io.micrometer:micrometer-core:1.12.0")

    // Caching
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Full-text search
    implementation("org.apache.lucene:lucene-core:9.8.0")
    implementation("org.apache.lucene:lucene-queryparser:9.8.0")
    implementation("org.apache.lucene:lucene-highlighter:9.8.0")
    implementation("org.apache.lucene:lucene-analysis-common:9.8.0")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
}