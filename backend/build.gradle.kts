val kotlin_version: String by project
val logback_version: String by project
val ktor_version: String by project

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
    maven {
        name = "Ebxml protokoll"
        url = uri("https://maven.pkg.github.com/navikt/ebxml-protokoll")
        credentials {
            username = "token"
            password = System.getenv("GITHUB_TOKEN")
        }
    }
    maven {
        name = "Emottak payload format"
        url = uri("https://maven.pkg.github.com/navikt/emottak-payload-xsd")
        credentials {
            username = "token"
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")

    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("no.nav.emottak:emottak-payload-xsd:0.0.2")
    implementation("no.nav.emottak:ebxml-protokoll:0.0.6")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.2")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks {

    shadowJar {
        archiveFileName.set("app.jar")
    }

    test {
        useJUnitPlatform()
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
        val isProduction: Boolean = System.getenv("NAIS_CLUSTER_NAME") != "prod-fss"
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
