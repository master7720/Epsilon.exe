import net.minecraftforge.gradle.userdev.UserDevExtension
import org.spongepowered.asm.gradle.plugins.MixinExtension

val library: Configuration by configurations.creating
val kotlinVersion: String by project
val kotlinxCoroutineVersion: String by project
val modGroup: String by extra
val modVersion: String by extra

group = modGroup
version = modVersion

buildscript {
    repositories {
        mavenCentral()
        maven("https://maven.minecraftforge.net/")
        maven("https://repo.spongepowered.org/maven/")
    }

    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:4.+")
        classpath("org.spongepowered:mixingradle:0.7-SNAPSHOT")
    }
}

plugins {
    java
    kotlin("jvm") version "1.6.0"
}

apply {
    plugin("net.minecraftforge.gradle")
    plugin("org.spongepowered.mixin")
}

repositories {
    mavenCentral()
    maven("https://impactdevelopment.github.io/maven/")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://jitpack.io")
}

configurations.implementation {
    extendsFrom(library)
}

val jarLib: Configuration by configurations.creating {
    extendsFrom(library)
}

dependencies {
    fun ModuleDependency.exclude(moduleName: String) =
        exclude(mapOf("module" to moduleName))

    "minecraft"("net.minecraftforge:forge:1.12.2-14.23.5.2860")

    library(kotlin("stdlib", kotlinVersion))
    library(kotlin("reflect", kotlinVersion))
    library(kotlin("stdlib-jdk8", kotlinVersion))
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutineVersion")

    library("org.joml:joml:1.10.5")
    library(fileTree("lib"))
    library("com.formdev:flatlaf:1.1.2")
    library("com.formdev:flatlaf-intellij-themes:1.1.2")

    annotationProcessor("org.spongepowered:mixin:0.8.2:processor")  {
        exclude("gson")
    }

    library("club.minnced:java-discord-rpc:v2.0.2") {
        exclude("jna")
    }

    library("org.reflections:reflections:0.9.12") {
        exclude("gson")
        exclude("guava")
    }

    library("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        exclude("commons-io")
        exclude("gson")
        exclude("guava")
        exclude("launchwrapper")
        exclude("log4j-core")
    }

    implementation("com.github.cabaletta:baritone:1.2.14")
    jarLib("cabaletta:baritone-api:1.2")
}

configure<MixinExtension> {
    defaultObfuscationEnv = "searge"
    add(sourceSets["main"], "mixins.epsilon.refmap.json")
}

configure<UserDevExtension> {
    mappings(
        mapOf(
            "channel" to "stable",
            "version" to "39-1.12"
        )
    )

    runs {
        create("client") {
            workingDirectory = project.file("run").path

            properties(
                mapOf(
                    "forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP",
                    "forge.logging.console.level" to "info",
                    "fml.coreMods.load" to "com.client.epsilon.launch.FMLCoreMod",
                    "mixin.env.disableRefMap" to "true"
                )
            )
        }
    }
}


tasks {
    compileJava {
        options.encoding = "UTF-8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlin.contracts.ExperimentalContracts",
                "-Xlambdas=indy",
                "-Xjvm-default=all",
                "-Xbackend-threads=0",
                "-Xinline-classes")
        }
    }

    artifacts {
        archives(register<Jar>("releaseJar") {
            group = "build"
            dependsOn("reobfJar")

            manifest {
                attributes(
                    "Manifest-Version" to 1.0,
                    "MixinConfigs" to "mixins.epsilon.json",
                    "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
                    "FMLCorePluginContainsFMLMod" to true,
                    "FMLCorePlugin" to "club.eridani.epsilon.client.EpsilonCoreMod",
                    "ForceLoadAsMod" to true
                )
            }

            val excludeDirs = listOf("META-INF/com.android.tools", "META-INF/maven", "META-INF/proguard", "META-INF/versions")
            val excludeNames = hashSetOf("module-info", "MUMFREY", "LICENSE", "kotlinx_coroutines_core")

            archiveClassifier.set("Release")

            from(
                jar.get().outputs.files.map {
                    if (it.isDirectory) it else zipTree(it)
                }
            )

            exclude { file ->
                file.name.endsWith("kotlin_module")
                        || excludeNames.contains(file.file.nameWithoutExtension)
                        || excludeDirs.any { file.path.contains(it) }
            }

            from(
                library.map {
                    if (it.isDirectory) it else zipTree(it)
                }
            )
        })
    }
}