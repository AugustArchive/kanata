/**
 * Microservice to handle Kubernetes pod state changes and reports them in Instatus or Statuspages.
 * Copyright (c) 2021 Noel <cutie@floofy.dev>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.Date
import java.text.SimpleDateFormat

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
        jcenter()
    }
}

plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("plugin.serialization") version "1.5.31"
    id("com.diffplug.spotless") version "5.14.0"
    kotlin("jvm") version "1.5.31"
    application
}

val current = Version(1, 0, 0)
group = "dev.floofy.services"
version = current.string()

repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
    maven {
        url = uri("https://maven.floofy.dev/repo/releases")
    }
}

dependencies {
    // Kotlin Libraries
    implementation(kotlin("stdlib", "1.5.31"))

    // Ktor (server, http)
    implementation("io.ktor:ktor-client-serialization:1.6.3")
    implementation("io.ktor:ktor-serialization:1.6.3")
    implementation("io.ktor:ktor-client-okhttp:1.6.3")
    implementation("io.ktor:ktor-server-netty:1.6.3")

    // Kotlinx.Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.1")

    // Logging (slf4j + logback)
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("ch.qos.logback:logback-core:1.2.6")
    api("org.slf4j:slf4j-api:1.7.32")

    // Kubernetes API
    implementation("io.kubernetes:client-java:13.0.0")

    // YAML
    implementation("com.charleskorn.kaml:kaml:0.36.0")

    // Haru (cron scheduler)
    implementation("dev.floofy.haru:Haru:1.2.0")
}

tasks.register("generateMetadata") {
    val path = sourceSets["main"].resources.srcDirs.first()
    if (!file(path).exists()) path.mkdirs()

    val date = Date()
    val formatter = SimpleDateFormat("MMM dd, YYYY - HH:mm:ss")

    file("$path/metadata.properties").writeText("""app.version = ${current.string()}
app.commit  = ${current.commit()}
app.build.date = ${formatter.format(date)}
""".trimIndent())
}

spotless {
    kotlin {
        trimTrailingWhitespace()
        licenseHeaderFile("${rootProject.projectDir}/assets/HEADING")
        endWithNewline()

        // We can't use the .editorconfig file, so we'll have to specify it here
        // issue: https://github.com/diffplug/spotless/issues/142
        // ktlint 0.35.0 (default for Spotless) doesn't support trailing commas
        ktlint("0.40.0")
            .userData(mapOf(
                "no-consecutive-blank-lines" to "true",
                "no-unit-return" to "true",
                "disabled_rules" to "no-wildcard-imports,colon-spacing",
                "indent_size" to "4"
            ))
    }
}

application {
    mainClass.set("dev.floofy.services.kanata.Bootstrap")
    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        kotlinOptions.javaParameters = true
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    named<ShadowJar>("shadowJar") {
        archiveFileName.set("Kanata.jar")
        mergeServiceFiles()

        manifest {
            attributes(mapOf(
                "Manifest-Version" to "1.0.0",
                "Main-Class" to "dev.floofy.services.kanata.Bootstrap"
            ))
        }
    }

    build {
        dependsOn("generateMetadata")
        dependsOn(spotlessApply)
        dependsOn(shadowJar)
    }
}

class Version(
    private val major: Int,
    private val minor: Int,
    private val patch: Int
) {
    fun string(): String = "$major.$minor.$patch"
    fun commit(): String = execShell("git rev-parse HEAD")
}

fun execShell(command: String): String {
    val parts = command.split("\\s".toRegex())
    val process = ProcessBuilder(*parts.toTypedArray())
        .directory(File("."))
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    process.waitFor(1, TimeUnit.MINUTES)
    return process.inputStream.bufferedReader().readText()
        .trim()
        .slice(0..8)
}
