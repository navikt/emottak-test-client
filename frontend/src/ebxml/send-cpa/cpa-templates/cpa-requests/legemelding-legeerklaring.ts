import { encodeXmlToBase64 } from "@/lib/utils";
import { EbxmlRequest } from "../../types";
import { v4 as uuidv4 } from "uuid";

export const legemeldingLegeerklaring: EbxmlRequest = {
  conversationId: uuidv4().toString(),
  messageId: uuidv4().toString(),
  fromPartyId: "13579",
  fromRole: "Lege",
  toPartyId: "79768",
  toRole: "Nav",
  cpaId: "nav:qass:36666",
  service: "Legemelding",
  action: "Legeerklring",
  signPayload: true,
  encryptPayload: true,
  useNewEmottakFlow: true,
  sendAsync: false,
  directSendin: true,
  ebxmlPayload: {
    base64Content: encodeXmlToBase64(
      `
    <MsgHead xmlns="http://www.kith.no/xmlstds/msghead/2006-05-24" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <MsgInfo>
            <Type DN="Vurdering av arbeidsmulighet / sykmelding" V="SYKMELD"/>
            <MIGversion>v1.2 2006-05-24</MIGversion>
            <GenDate>2026-05-19T10:11:49</GenDate>
            <MsgId>FB44D4D2-DECB-4566-A335-CBED9133925E</MsgId>
            <ProcessingStatus DN="Produksjon" V="P"/>
            <Ack DN="Ja" V="J"/>
            <Sender xmlns="http://www.kith.no/xmlstds/msghead/2006-05-24" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <Organisation>
                    <OrganisationName>Oslo kommune</OrganisationName>
                    <Ident>
                        <Id>997506499</Id>
                        <TypeId DN="Organisasjonsnummeret i Enhetsregister" S="2.16.578.1.12.4.1.1.9051" V="ENH"/>
                    </Ident>
                    <Ident>
                        <Id>50087</Id>
                        <TypeId DN="HER-id" S="2.16.578.1.12.4.1.1.9051" V="HER"/>
                    </Ident>
                    <Address>
                        <Type DN="Postadresse" V="PST"/>
                        <StreetAdr>Trondheimsveien 233</StreetAdr>
                        <PostalCode>0586</PostalCode>
                        <City>OSLO</City>
                    </Address>
                    <HealthcareProfessional>
                        <RoleToPatient DN="Journalansvarlig" S="2.16.578.1.12.4.1.1.9034" V="4"/>
                        <FamilyName>Prest</FamilyName>
                        <GivenName>Nett</GivenName>
                        <Ident>
                            <Id>03056537693</Id>
                            <TypeId DN="Fødselsnummer" S="2.16.578.1.12.4.1.1.8116" V="FNR"/>
                        </Ident>
                        <Ident>
                            <Id>565566567</Id>
                            <TypeId DN="HPR-nummer" S="2.16.578.1.12.4.1.1.8116" V="HPR"/>
                        </Ident>
                    </HealthcareProfessional>
                </Organisation>
            </Sender>
            <Receiver xmlns="http://www.kith.no/xmlstds/msghead/2006-05-24" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <Organisation>
                    <OrganisationName>NAV IKT</OrganisationName>
                    <Ident>
                        <Id>79768</Id>
                        <TypeId V="HER" S="2.16.578.1.12.4.1.1.9051" DN="HER-id"/>
                    </Ident>
                </Organisation>
            </Receiver>
            <Patient xmlns="http://www.kith.no/xmlstds/msghead/2006-05-24" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <FamilyName>HJELM</FamilyName>
                <GivenName>FASCINERENDE</GivenName>
                <Sex V="1" DN="Mann"/>
                <Ident>
                    <Id>16894698199</Id>
                    <TypeId V="FNR" S="2.16.578.1.12.4.1.1.8116" DN="Fødselsnummer"/>
                </Ident>
           </Patient>
        </MsgInfo>
        <Document>
            <RefDoc>
                <MsgType DN="XML-instans" V="XML"/>
                <Content>
                    <HelseOpplysningerArbeidsuforhet xmlns="http://www.kith.no/xmlstds/HelseOpplysningerArbeidsuforhet/2013-10-01" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                        <SyketilfelleStartDato>2026-05-19</SyketilfelleStartDato>
                        <Pasient>
                            <Navn>
                                <Etternavn>HJELM</Etternavn>
                                <Fornavn>FASCINERENDE</Fornavn>
                            </Navn>
                            <Fodselsnummer>
                                <Id>16894698199</Id>
                                <TypeId DN="Testversjon av FNR, reserverte data" S="2.16.578.1.12.4.1.1.8116"
                                        V="FNR_Test_Reserved"/>
                            </Fodselsnummer>
                            <KontaktInfo>
                                <TypeTelecom DN="Mobiltelefon" V="MC"/>
                                <TeleAddress V="tel:4746516297"/>
                            </KontaktInfo>
                        </Pasient>
                        <Arbeidsgiver>
                            <HarArbeidsgiver DN="Ingen arbeidsgiver" V="3"/>
                        </Arbeidsgiver>
                        <MedisinskVurdering>
                            <HovedDiagnose>
                                <Diagnosekode DN="MAGESMERTER AKUTT" S="2.16.578.1.12.4.1.1.7170" V="D01"/>
                            </HovedDiagnose>
                        </MedisinskVurdering>
                        <Aktivitet>
                            <Periode>
                                <PeriodeFOMDato>2026-05-19</PeriodeFOMDato>
                                <PeriodeTOMDato>2026-05-21</PeriodeTOMDato>
                                <AvventendeSykmelding>
                                    <InnspillTilArbeidsgiver>tetetet</InnspillTilArbeidsgiver>
                                </AvventendeSykmelding>
                            </Periode>
                        </Aktivitet>
                        <Prognose>
                            <ArbeidsforEtterEndtPeriode>false</ArbeidsforEtterEndtPeriode>
                            <ErIkkeIArbeid/>
                        </Prognose>
                        <MeldingTilArbeidsgiver/>
                        <KontaktMedPasient>
                            <BehandletDato>2026-05-19T09:10:17</BehandletDato>
                        </KontaktMedPasient>
                        <Behandler>
                            <Navn>
                                <Etternavn>FJELLSTAD</Etternavn>
                                <Fornavn>ESPEN</Fornavn>
                            </Navn>
                            <Id>
                                <Id>03056537693</Id>
                                <TypeId DN="Fødselsnummer" S="2.16.578.1.12.4.1.1.8116" V="FNR"/>
                            </Id>
                            <Id>
                                <Id>56091</Id>
                                <TypeId DN="HPR-nummer" S="2.16.578.1.12.4.1.1.8116" V="HPR"/>
                            </Id>
                            <Id>
                                <Id/>
                                <TypeId DN="HER-id" S="2.16.578.1.12.4.1.1.8116" V="HER"/>
                            </Id>
                            <Adresse>
                                <Type DN="Postadresse" V="PST"/>
                                <StreetAdr>Trondheimsveien 233</StreetAdr>
                                <PostalCode>0586</PostalCode>
                                <City>OSLO</City>
                                <County/>
                                <Country/>
                            </Adresse>
                            <KontaktInfo>
                                <TypeTelecom DN="Arbeidsplass" V="WP"/>
                                <TeleAddress V="tel:116117"/>
                            </KontaktInfo>
                        </Behandler>
                        <AvsenderSystem>
                            <SystemNavn>CGM Vision</SystemNavn>
                            <SystemVersjon>6.1A.24678</SystemVersjon>
                        </AvsenderSystem>
                        <Strekkode>123456789012345678901210520260</Strekkode>
                    </HelseOpplysningerArbeidsuforhet>
                </Content>
            </RefDoc>
        </Document>
    </MsgHead>
    `.trim()
    ),
  },
};
