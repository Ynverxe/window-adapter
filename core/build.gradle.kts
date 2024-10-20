plugins {
    id("java")
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