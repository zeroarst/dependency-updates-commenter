import org.gradle.internal.impldep.com.google.api.services.storage.model.Bucket.Website

group = property("GROUP").toString()
version = property("VERSION").toString()

plugins {
    kotlin("jvm") version "1.7.21"
    `maven-publish`// deploy to Maven local repository
    `java-gradle-plugin`
    // `kotlin-dsl` // shorthand for id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish") version "1.0.0"
}

repositories {
    mavenCentral()
}


dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-jaxb:2.9.0")

    implementation("org.json:json:20220924")

    implementation("junit:junit:4.13.2")

}

gradlePlugin {
    plugins {
        create(property("ID").toString()) {
            id = property("ID").toString()
            implementationClass = property("IMPLEMENTATION_CLASS").toString()
            displayName = property("DISPLAY_NAME").toString()
        }
    }
}


// Configuration Block for the Plugin Marker artifact on Plugin Central
pluginBundle {
    website = property("WEBSITE").toString()
    vcsUrl = property("VCS_URL").toString()
    description = property("DESCRIPTION").toString()
    tags = listOf("plugin", "gradle", "dependency-updates", "new-versions")
}