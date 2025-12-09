val kotlin_version: String by project
val logback_version: String by project
val ktor_version: String by project
val koin_version: String by project

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    id("io.ktor.plugin") version "3.1.2"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

group = "no.nav.emottak"
version = "0.0.1"

application {
    mainClass.set("no.nav.emottak.test.client.AppKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    exclusiveContent {
        // emottak-payload-xsd depends on org.apache.cxf:cxf-rt-ws-security:4.1.4 which depends on opensaml-saml-impl:5.1.6
        // This is not available in maven central
        forRepository {
            maven {
                name = "Shibboleth"
                url = uri("https://build.shibboleth.net/maven/releases/")
            }
        }
        filter {
            // Only allow specific group/artifact from Shibboleth
            includeGroup("org.opensaml")
            includeGroup("net.shibboleth")
            // Add more includeGroup or includeModule as needed
        }
    }
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
    implementation(platform("io.ktor:ktor-bom:$ktor_version"))

    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-xml-jvm")

    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-client-logging-jvm")

    implementation("net.logstash.logback:logstash-logback-encoder:8.1")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("org.codehaus.janino:janino:3.1.6")
    implementation("no.nav.emottak:emottak-utils:0.3.2")
    implementation("no.nav.emottak:emottak-payload-xsd:0.0.11")
    implementation("no.nav.emottak:ebxml-protokoll:0.0.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
    implementation("org.apache.santuario:xmlsec:3.0.3")

    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-core")

    implementation("com.sksamuel.hoplite:hoplite-core:2.8.2")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.8.2")

    implementation("io.arrow-kt:arrow-core:2.1.0")
    implementation("io.arrow-kt:arrow-fx-coroutines:2.1.0")
    implementation("io.arrow-kt:suspendapp:2.1.0")
    implementation("io.arrow-kt:suspendapp-ktor:2.1.0")

    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.mockk:mockk:1.13.5")
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
        environment("VIRKSOMHETSSERTIFIKAT_SIGNERING", System.getenv("VIRKSOMHETSSERTIFIKAT_SIGNERING"))
        environment("VIRKSOMHETSSERTIFIKAT_CREDENTIALS", System.getenv("VIRKSOMHETSSERTIFIKAT_CREDENTIALS"))
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
