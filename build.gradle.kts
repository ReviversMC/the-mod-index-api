plugins {
    id ("com.github.johnrengelman.shadow") version "7.1.2"
    java
}

group = "com.github.reviversmc.themodindex.api"
version = "1.0.0+1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.moshi:moshi:1.13.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
}

tasks {
    compileJava {
        options.release.set(17)
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    shadowJar {
        archiveFileName.set(rootProject.name + "-" + rootProject.version + ".jar")
    }
}