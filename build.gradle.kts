plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
    alias(libs.plugins.run.paper)
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.bundles.base)
    implementation(project(":nms-common"))
    implementation(project(":nms-1.20"))
    implementation(project(":nms-1.20.5"))
    implementation(project(":nms-1.21"))
}