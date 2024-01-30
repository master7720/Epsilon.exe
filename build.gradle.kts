import net.minecraftforge.gradle.userdev.UserDevExtension
import org.jetbrains.kotlin.konan.properties.loadProperties
import org.spongepowered.asm.gradle.plugins.MixinExtension

group = "club.epsilon"
version = "4.0"

buildscript {
    repositories {
        maven("https://files.minecraftforge.net/maven")
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }

    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:4.+")
        classpath("org.spongepowered:mixingradle:0.7-SNAPSHOT")
    }
}

plugins {
    idea
    java
    id("org.jetbrains.kotlin.jvm") version "1.6.0"
    id("java-library")
}

apply {
    plugin("net.minecraftforge.gradle")
    plugin("org.spongepowered.mixin")
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://impactdevelopment.github.io/maven/")
}

val library: Configuration by configurations.creating

val kotlinxCoroutineVersion: String by project
val minecraftVersion: String by project
val forgeVersion: String by project
val mappingsChannel: String by project
val mappingsVersion: String by project

dependencies {
    // Jar packaging
    fun ModuleDependency.exclude(moduleName: String): ModuleDependency {
        return exclude(mapOf("module" to moduleName))
    }

    fun jarOnly(dependencyNotation: Any) {
        library(dependencyNotation)
    }

    // Forge
    val minecraft = "minecraft"
    minecraft("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")

    // Dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutineVersion")

    implementation("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        exclude("commons-io")
        exclude("gson")
        exclude("guava")
        exclude("launchwrapper")
        exclude("log4j-core")
    }

    annotationProcessor("org.spongepowered:mixin:0.8.2:processor") {
        exclude("gson")
    }

    implementation("club.minnced:java-discord-rpc:v2.0.2") {
        exclude("jna")
    }

    implementation("org.reflections:reflections:0.9.12") {
        exclude("gson")
        exclude("guava")
    }

    implementation("org.joml:joml:1.10.2")

    implementation("org.apache.logging.log4j:log4j-api:2.17.1")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")

    implementation(fileTree("lib"))

    implementation("com.formdev:flatlaf:1.1.2")
    implementation("com.formdev:flatlaf-intellij-themes:1.1.2")

    implementation("com.github.cabaletta:baritone:1.2.14")
    jarOnly("cabaletta:baritone-api:1.2")
}

configure<MixinExtension> {
    add(sourceSets["main"], "mixins.epsilon.refmap.json")
}

configure<UserDevExtension> {
    mappings(
        mapOf(
            "channel" to mappingsChannel,
            "version" to mappingsVersion
        )
    )

    runs {
        create("client") {
            workingDirectory = project.file("run").path
            ideaModule("${rootProject.name}.${project.name}.main")

            properties(
                mapOf(
                    "forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP",
                    "forge.logging.console.level" to "info",
                    "fml.coreMods.load" to "club.eridani.epsilon.client.EpsilonCoreMod",
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
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlin.contracts.ExperimentalContracts",
                "-Xlambdas=indy"
            )
        }
    }

    jar {
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

        val regex = "baritone-1\\.2\\.\\d\\d\\.jar".toRegex()
        from(
            (configurations.runtimeClasspath.get() - configurations["minecraft"])
                .filterNot {
                    it.name.matches(regex)
                }.map {
                    if (it.isDirectory) it else zipTree(it)
                }
        )

        from(
            library.map {
                if (it.isDirectory) it else zipTree(it)
            }
        )
    }
}