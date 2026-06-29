package no.nav.emottak.test.client.application.ebxml.usecases

class TemplateValues(val cpaId: String, val partnerId: Long, val signedOf: String)

const val STANDARD_CPA = "nav:qass:36666"

public val defaultTemplateValues = TemplateValues(STANDARD_CPA, 0L, "")
public val sykmeldingTemplateValues = TemplateValues(STANDARD_CPA, 0L, "06758809488")

public val dialogmoteinnkallingTemplateValues = TemplateValues(STANDARD_CPA, 14629L, "06758809488")
public val henvendelsefralegeTemplateValues = TemplateValues(STANDARD_CPA, 13123L, "01010112377")
public val foresporselfrasaksbehandlerTemplateValues = TemplateValues(STANDARD_CPA, 12049L, "21312341414")

fun getTemplateValues(service: String): TemplateValues {
    return when (service) {
        "Sykmelding" -> sykmeldingTemplateValues
        "DialogmoteInnkalling" -> dialogmoteinnkallingTemplateValues
        "HenvendelseFraLege" -> henvendelsefralegeTemplateValues
        "ForesporselFraSaksbehandler" -> foresporselfrasaksbehandlerTemplateValues
        else -> defaultTemplateValues
    }
}
