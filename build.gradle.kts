plugins {
    id ("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    id("org.jetbrains.dokka") version "1.6.21"
    `maven-publish`
}

group = "com.github.reviversmc.themodindex.api"
version = "3.0.0"

repositories {
    mavenCentral()
}

dependencies {
    api("com.squareup.okhttp3:okhttp:4.9.3")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    testApi(kotlin("test"))
    testApi("com.squareup.okhttp3:okhttp:4.9.3")
    testApi("com.github.gmazzo:okhttp-mock:1.5.0")
    testApi("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
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
