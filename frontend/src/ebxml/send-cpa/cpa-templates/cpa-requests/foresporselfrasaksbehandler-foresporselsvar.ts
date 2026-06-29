import { encodeXmlToBase64 } from "@/lib/utils";
import { EbxmlRequest } from "../../types";
import { v4 as uuidv4 } from "uuid";

export const foresporselfrasaksbehandlerForesporselsvar: EbxmlRequest = {
  conversationId: uuidv4().toString(),
  messageId: uuidv4().toString(),
  fromPartyId: "13579",
  fromRole: "Sykmelder",
  toPartyId: "79768",
  toRole: "Saksbehandler",
  cpaId: "nav:qass:36666",
  service: "ForesporselFraSaksbehandler",
  action: "ForesporselSvar",
  signPayload: true,
  encryptPayload: true,
  useNewEmottakFlow: true,
  sendAsync: true,
  ebxmlPayload: {
    base64Content: encodeXmlToBase64(
      `
    <MsgHead xmlns="http://www.kith.no/xmlstds/msghead/2006-05-24" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <MsgInfo>
            <Type DN="Svar på forespørsel" V="DIALOG_SVAR"/>
            <MIGversion>v1.2 2006-05-24</MIGversion>
            <GenDate>2020-09-30T18:00:31.4764563+02:00</GenDate>
            <MsgId>b62016eb-6c2d-417a-8ecc-157b3c5ee2ca</MsgId>
            <Ack DN="Ja" V="J"/>
            <Sender>
                <Organisation>
                    <OrganisationName>Kule helsetjenester AS</OrganisationName>
                    <Ident>
                        <Id>223456789</Id>
                        <TypeId DN="Organisasjonsnummeret i Enhetsregister" S="2.16.578.1.12.4.1.1.9051" V="ENH"/>
                    </Ident>
                    <Ident>
                        <Id>0123</Id>
                        <TypeId DN="HER-id" S="2.16.578.1.12.4.1.1.9051" V="HER"/>
                    </Ident>
                    <Address>
                        <Type DN="Postadresse" V="PST"/>
                        <StreetAdr>Oppdiktet gate 203</StreetAdr>
                        <PostalCode>1234</PostalCode>
                        <City>Oslo</City>
                        <County DN="Oslo" V="0712"/>
                        <Country DN="Norge" V="NO"/>
                    </Address>
                    <TeleCom>
                        <TypeTelecom DN="Arbeidsplass" V="WP"/>
                        <TeleAddress V="tel:11223344"/>
                    </TeleCom>
                    <HealthcareProfessional>
                        <TypeHealthcareProfessional DN="Lege" V="LE"/>
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
                        <Id>889640782</Id>
                        <TypeId DN="Organisasjonsnummeret i Enhetsregister" S="2.16.578.1.12.4.1.1.9051" V="ENH"/>
                    </Ident>
                    <Ident>
                        <Id>79768</Id>
                        <TypeId DN="HER-id" S="2.16.578.1.12.4.1.1.9051" V="HER"/>
                    </Ident>
                    <Organisation>
                        <OrganisationName>NAV Drammen</OrganisationName>
                        <Ident>
                            <Id>0602</Id>
                            <TypeId DN="Lokal identifikator for institusjoner" S="2.16.578.1.12.4.1.1.9051" V="LIN"/>
                        </Ident>
                    </Organisation>
                </Organisation>
            </Receiver>
            <Patient>
                <FamilyName>Test</FamilyName>
                <GivenName>Etternavn</GivenName>
                <Ident>
                    <Id>3143242342</Id>
                    <TypeId DN="Fødselsnummer" S="2.16.578.1.12.4.1.1.8116" V="FNR"/>
                </Ident>
                <Address>
                    <Type DN="Folkeregisteradresse" V="HP"/>
                    <StreetAdr>Sannergata 2</StreetAdr>
                    <PostalCode>0655</PostalCode>
                    <City>OSLO</City>
                    <County DN="OSLO" V="0712" />
                    <Country DN="Norge" V="NO"/>
                </Address>
                <TeleCom>
                    <TypeTelecom DN="Mobiltelefon" V="MC"/>
                    <TeleAddress V="tel:11223344"/>
                </TeleCom>
                <TeleCom>
                    <TypeTelecom DN="Hjemme" V="H"/>
                    <TeleAddress V="tel:44332211"/>
                </TeleCom>
            </Patient>
        </MsgInfo>
        <Document>
            <RefDoc>
                <IssueDate V="2020-09-30T18:00:31"/>
                <MsgType DN="XML-instans" V="XML"/>
                <Content>
                    <Dialogmelding xmlns="http://www.kith.no/xmlstds/dialog/2006-10-11" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                        <Notat>
                            <TemaKodet DN="Svar på forespørsel" S="2.16.578.1.12.4.1.1.9069" V="5"/>
                            <TekstNotatInnhold xsi:type="xsd:string">Pasieten har masse info her</TekstNotatInnhold>
                            <DokIdNotat>OD1812186729156</DokIdNotat>
                            <Foresporsel>
                                <TypeForesp DN="Påminnelse forespørsel om pasient" S="2.16.578.1.12.4.1.1.8129" V="2"/>
                                <Sporsmal>
                                    Påminnelse
                                    Test Etternavn
                                    Vi viser til tidligere forespørsel angående din pasient.
                                    Vi kan ikke se å ha mottatt svar på vår henvendelse og ber om at vår anmodning om opplysninger angående
                                    ovennevnte pasient besvares snarest.
                                    Hvis opplysningene er sendt oss i løpet av de siste dagene, kan du se bort fra dette brevet.
                                    Med hilsen
                                    NAV
                                    Jon Person
                                </Sporsmal>
                                <DokIdForesp>OD1812186729156</DokIdForesp>
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
