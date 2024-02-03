rootProject.name = "TrollHack"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.luna5ama.dev/")
        maven("https://files.minecraftforge.net/maven")
    }

    val kotlinVersion: String by settings
    val forgeGradleVersion: String by settings
    val kspVersion: String by settings
    val kmogusVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion

        id("net.minecraftforge.gradle") version forgeGradleVersion

        id("com.google.devtools.ksp") version kspVersion
        id("dev.luna5ama.kmogus-struct-plugin") version kmogusVersion
    }

    resolutionStrategy.eachPlugin {
        if (requested.id.id == "net.minecraftforge.gradle.ForgeGradle") {
            useModule("net.minecraftforge.gradle:ForgeGradle:${requested.version}")
        }
    }
}

include("structs")