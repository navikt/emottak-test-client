package no.nav.emottak.test.client.application.ebxml.usecases

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addEnvironmentSource
import com.sksamuel.hoplite.addResourceSource
import no.nav.emottak.test.client.infrastructure.config.ApplicationConfig

@OptIn(ExperimentalHoplite::class)
fun testConfig() = ConfigLoader.builder()
    .addEnvironmentSource()
    .addResourceSource("/application-test.yml")
    .withExplicitSealedTypes()
    .build()
    .loadConfigOrThrow<ApplicationConfig>()
