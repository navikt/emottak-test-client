val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.0.10"
    id("io.ktor.plugin") version "2.3.12"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

group = "no.nav.emottak"
version = "0.0.1"

application {
    mainClass.set("no.nav.emottak.test.client.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks {

    shadowJar {
        archiveFileName.set("app.jar")
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("failed")
            showStandardStreams = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }

    ktlintFormat {
        this.enabled = true
    }
    ktlintCheck {
        dependsOn("ktlintFormat")
    }
    build {
        dependsOn("ktlintCheck")
    }

    withType<JavaExec> {
        val isProduction: Boolean = System.getenv("PRODUCTION")?.toBoolean() ?: false
        val isDevelopment: Boolean = !isProduction
        jvmArgs = listOf("-Dio.ktor.development=$isDevelopment")

        if (isDevelopment) {
            doFirst {
                println("Development mode enabled. Starting continuous build with hot-reloading...")

                Thread {
                    exec {
                        commandLine("./gradlew", "build", "--continuous")
                        isIgnoreExitValue = true
                    }
                }.start()

                Thread.sleep(2000)
            }
        }
    }
}
