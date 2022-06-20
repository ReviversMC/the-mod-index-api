plugins {
    id ("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("org.jetbrains.dokka") version "1.7.0"
    `maven-publish`
}

group = "com.github.reviversmc.themodindex.api"
version = "7.1.0"

repositories {
    mavenCentral()
}

dependencies {
    api("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    testApi(kotlin("test"))
    testApi("com.squareup.okhttp3:mockwebserver:4.10.0")
}

tasks {
    compileJava {
        options.release.set(17)
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    compileTestJava {
        options.release.set(17)
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    dokkaHtml {
        dokkaSourceSets {
            configureEach {
                jdkVersion.set(17)
            }
        }
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    publishing {
        publications {
            create<MavenPublication>("api") {
                from(rootProject.components["kotlin"])
            }
        }
    }

    shadowJar {
        archiveFileName.set(rootProject.name + "-" + rootProject.version + ".jar")
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
