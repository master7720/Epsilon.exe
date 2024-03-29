import net.minecraftforge.gradle.userdev.UserDevExtension
import org.spongepowered.asm.gradle.plugins.MixinExtension

val library: Configuration by project
val kotlinVersion: String by project
val kotlinxCoroutineVersion: String by project
val modGroup: String by project
val modVersion: String by project
val kmogusVersion: String by project
val minecraftVersion: String by project
val forgeVersion: String by project

group = modGroup
version = modVersion

buildscript {
    repositories {
        mavenCentral()
        maven("https://maven.luna5ama.dev/")
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://jitpack.io/")
        maven("https://impactdevelopment.github.io/maven/")
    }

    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:4.+")
        classpath("org.spongepowered:mixin:0.7.11-SNAPSHOT")
    }
}

plugins {
    idea
    java
    kotlin("jvm")
    id("net.minecraftforge.gradle")
    id("com.google.devtools.ksp")
    id("dev.luna5ama.kmogus-struct-plugin") apply false
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

    library("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")

    library(kotlin("org.jetbrains.kotlin-reflect", kotlinVersion))
    library("org.jetbrains.kotlin:kotlin-stdlib")
    library("org.jetbrains.kotlin:kotlin-stdlib-jdk7")
    library("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutineVersion")

    library("dev.luna5ama:kmogus-core:$kmogusVersion")
    library("dev.luna5ama:kmogus-struct-api:$kmogusVersion")
    library(project(":structs"))

    library("dev.fastmc:fastmc-common:1.1-SNAPSHOT:java8")

    library("org.spongepowered:mixin:0.7.11-SNAPSHOT")

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