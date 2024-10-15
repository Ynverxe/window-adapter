plugins {
    id("java")
    alias(libs.plugins.paperweight.userdev)
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")

    compileOnly(libs.bundles.base)
    implementation(project(":nms-common"))
}