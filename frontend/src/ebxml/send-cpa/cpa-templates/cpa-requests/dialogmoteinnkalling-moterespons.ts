import { encodeXmlToBase64 } from "@/lib/utils";
import { EbxmlRequest } from "../../types";
import { v4 as uuidv4 } from "uuid";

export const dialogmoteInnkallingMoteRespons: EbxmlRequest = {
  conversationId: uuidv4().toString(),
  messageId: uuidv4().toString(),
  fromPartyId: "13579",
  fromRole: "Sykmelder",
  toPartyId: "79768",
  toRole: "Saksbehandler",
  cpaId: "nav:qass:36666",
  service: "DialogmoteInnkalling",
  action: "MoteRespons",
  signPayload: true,
  encryptPayload: true,
  useNewEmottakFlow: true,
  sendAsync: true,
  ebxmlPayload: {
    base64Content: encodeXmlToBase64(
      `<MsgHead
            xmlns="http://www.kith.no/xmlstds/msghead/2006-05-24" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.kith.no/xmlstds/msghead/2006-05-24 MsgHead-v1_2.xsd">
        <MsgInfo>
            <Type DN="Svar p.. foresp..rsel" V="DIALOG_SVAR"/>
            <MIGversion>v1.2 2006-05-24</MIGversion>
            <GenDate>2020-09-21T21:11:55+08:00</GenDate>
            <MsgId>9be88bc5-4219-473e-954b-c0dd115ff4e0</MsgId>
            <Ack DN="Ja" V="J"/>
            <ConversationRef>
                <RefToParent>d97c669d-484b-42ef-b4e9-79d0f290a71f</RefToParent>
                <RefToConversation>d97c669d-484b-42ef-b4e9-79d0f290a71f</RefToConversation>
            </ConversationRef>
            <Sender>
                <Organisation>
                    <OrganisationName>Kule helsetjenester AS</OrganisationName>
                    <Ident>
                        <Id>0123</Id>
                        <TypeId DN="HER-id" S="2.16.578.1.12.4.1.1.9051" V="HER"/>
                    </Ident>
                    <Ident>
                        <Id>223456789</Id>
                        <TypeId DN="Organisasjonsnummeret i Enhetsregister" S="2.16.578.1.12.4.1.1.9051" V="ENH"/>
                    </Ident>
                    <Address>
                        <Type DN="POSTADRESSE" V="PST"/>
                        <StreetAdr>Oppdiktet gate 203</StreetAdr>
                        <PostalCode>1234</PostalCode>
                        <City>Oslo</City>
                    </Address>
                    <TeleCom>
                        <TypeTelecom DN="Arbeidsplass" V="WP"/>
                        <TeleAddress V="tel:12 34 56 78"/>
                    </TeleCom>
                    <TeleCom>
                        <TypeTelecom DN="Fax" V="F"/>
                        <TeleAddress V="fax:87 65 43 21"/>
                    </TeleCom>
                    <HealthcareProfessional>
                        <FamilyName>Valda</FamilyName>
                        <MiddleName>Fos</MiddleName>
                        <GivenName>Inga</GivenName>
                        <Ident>
                            <Id>1234567</Id>
                            <TypeId DN="HPR-nummer" S="2.16.578.1.12.4.1.1.8116" V="HPR"/>
                        </Ident>
                        <Ident>
                            <Id>1234</Id>
                            <TypeId DN="HER-id" S="2.16.578.1.12.4.1.1.8116" V="HER"/>
                        </Ident>
                    </HealthcareProfessional>
                </Organisation>
            </Sender>
            <Receiver>
                <Organisation>
                    <OrganisationName>NAV</OrganisationName>
                    <Ident>
                        <Id>79768</Id>
                        <TypeId DN="Identifikator fra Helsetjenesteenhetsregisteret (HER-id)" S="2.16.578.1.12.4.1.1.9051" V="HER"/>
                    </Ident>
                    <Ident>
                        <Id>889640782</Id>
                        <TypeId DN="Organisasjonsnummeret i Enhetsregisteret" S="2.16.578.1.12.4.1.1.9051" V="ENH"/>
                    </Ident>
                    <Organisation>
                        <OrganisationName>NAV Oslo</OrganisationName>
                        <Ident>
                            <Id>0000</Id>
                            <TypeId DN="Lokal identifikator for institusjoner" S="2.16.578.1.12.4.1.1.9051" V="LIN"/>
                        </Ident>
                    </Organisation>
                </Organisation>
            </Receiver>
            <Patient>
                <FamilyName>Test</FamilyName>
                <GivenName>Etternavn</GivenName>
                <DateOfBirth>1991-12-4</DateOfBirth>
                <Sex DN="Mann" V="1" />
                <Ident>
                    <Id>01010142365</Id>
                    <TypeId DN="Fødselsnummer" S="2.16.578.1.12.4.1.1.8116" V="FNR" />
                </Ident>
                <Address>
                    <Type DN="Postadresse" V="PST" />
                    <StreetAdr>Sannergata 2</StreetAdr>
                    <PostalCode>0655</PostalCode>
                    <City>OSLO</City>
                    <County DN="OSLO" V="0712" />
                </Address>
            </Patient>
        </MsgInfo>
        <Document>
            <RefDoc>
                <IssueDate V="2020-09-21T21:11:26"/>
                <MsgType DN="XML-instans" V="XML"/>
                <Content>
                    <Dialogmelding
                            xmlns="http://www.kith.no/xmlstds/dialog/2006-10-11" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.kith.no/xmlstds/dialog/2006-10-11 dialogmelding.xsd">
                        <Notat>
                            <TemaKodet DN="Ja, jeg kommer" S="2.16.578.1.12.4.1.1.8126" V="1"/>
                            <TekstNotatInnhold>Ta gjerne kontakt ang hvilket telefonnummer jeg skal ringe. Mvh Inga F. Valda.</TekstNotatInnhold>
                            <DokIdNotat>OD2009169905747</DokIdNotat>
                            <DatoNotat>2020-09-21</DatoNotat>
                            <Foresporsel>
                                <TypeForesp DN="Endring dialogm..te 2" S="2.16.578.1.12.4.1.1.8125" V="2"/>
                                <Sporsmal>
                                    Endret møtetidspunkt for dialogmøteGjelder Test Etternavn.NAV har tidligere innkalt til et dialogmøte angående din pasient. Møtet skulle vært
                                    avholdt 03.09.2020 klokken 14:00.Dette møtet må flyttes, og vi foreslår nytt møtetidspunkt 22.09.2020 klokken 12:00.Møtested: SvingenHvis tidspunktet ikke
                                    passer ber vi om en rask tilbakemelding.Vi gjør oppmerksom på at det er obligatorisk å delta i dialogmøter med NAV med mindre det er gyldig grunn for
                                    fravær.Fastlege deltar pr telefon. Med hilsenNAVJon PersonAktuelle lover: Både folketrygdloven og arbeidsmiljøloven har bestemmelser om
                                    sykefraværsoppfølging:Folketrygdloven § 8-4 og § 8-7Arbeidsmiljøloven § 4-6InformasjonØnsker du mer informasjon om regler og ordninger for oppfølging av
                                    sykmeldte?www.nav.no og www.arbeidstilsynet.no Lokalt NAV-kontor
                                </Sporsmal>
                                <DokIdForesp>OD2009169905747</DokIdForesp>
                                <RollerRelatertNotat>
                                    <RolleNotat S="2.16.578.1.12.4.1.1.9057" V="1"/>
                                    <Person>
                                        <GivenName>Jon</GivenName>
                                        <FamilyName>Person</FamilyName>
                                    </Person>
                                </RollerRelatertNotat>
                            </Foresporsel>
                        </Notat>
                    </Dialogmelding>
                </Content>
            </RefDoc>
        </Document>
    </MsgHead>
    `.trim()
    ),
  },
};
