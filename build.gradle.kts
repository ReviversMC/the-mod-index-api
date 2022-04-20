plugins {
    id ("com.github.johnrengelman.shadow") version "7.1.2"
    `java-library`
    `maven-publish`
}

group = "com.github.reviversmc.themodindex.api"
version = "1.0.0-1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    api("com.squareup.moshi:moshi:1.13.0")
    api("com.squareup.okhttp3:okhttp:4.9.3")

    testImplementation("com.squareup.moshi:moshi:1.13.0")
    testImplementation("com.squareup.okhttp3:okhttp:4.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks {
    compileJava {
        options.release.set(17)
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    publishing {
        publications {
            create<MavenPublication>("api") {
                from(rootProject.components["java"])
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