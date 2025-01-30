package no.nav.emottak.test.client.infrastructure.utils

fun getEnvVar(varName: String, defaultValue: String? = null) =
    System.getenv(varName) ?: System.getProperty(varName) ?: defaultValue
        ?: throw RuntimeException("Environment: Missing required variable \"$varName\"")
