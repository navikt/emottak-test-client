import { encodeXmlToBase64 } from "@/lib/utils";
import { EbxmlRequest } from "../../types";
import { v4 as uuidv4 } from "uuid";

export const henvendelsefralegeHenvendelse: EbxmlRequest = {
  conversationId: uuidv4().toString(),
  messageId: uuidv4().toString(),
  fromPartyId: "13579",
  fromRole: "Sykmelder",
  toPartyId: "79768",
  toRole: "Saksbehandler",
  cpaId: "nav:qass:36666",
  service: "HenvendelseFraLege",
  action: "Henvendelse",
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
            <Type DN="Notat" V="DIALOG_NOTAT" />
            <MIGversion>v1.2 2006-05-24</MIGversion>
            <GenDate>2019-01-16T22:51:35.5317672+01:00</GenDate>
            <MsgId>37340D30-FE14-42B5-985F-A8FF8FFA0CB5</MsgId>
            <ConversationRef>
                <RefToConversation>37340D30-FE14-42B5-985F-A8FF8FFA0C99</RefToConversation>
                <RefToParent>37340D30-FE14-42B5-985F-A8FF8FFA0CB5</RefToParent>
            </ConversationRef>
            <Ack DN="Ja" V="J" />
            <Sender>
                <Organisation>
                    <OrganisationName>Kule helsetjenester AS</OrganisationName>
                    <Ident>
                        <Id>223456789</Id>
                        <TypeId DN="Organisasjonsnummeret i Enhetsregister (Brønnøysund)" S="1.16.578.1.12.3.1.1.9051" V="ENH" />
                    </Ident>
                    <Ident>
                        <Id>0123</Id>
                        <TypeId DN="Identifikator fra Helsetjenesteenhetsregisteret (HER-id)" V="HER" S="1.23.456.7.89.1.2.3.4567.8912" />
                    </Ident>
                    <Address>
                        <StreetAdr>Oppdiktet gate 203</StreetAdr>
                        <PostalCode>1234</PostalCode>
                        <City>Oslo</City>
                    </Address>
                    <Organisation/>
                    <HealthcareProfessional>
                        <FamilyName>Valda</FamilyName>
                        <MiddleName>Fos</MiddleName>
                        <GivenName>Inga</GivenName>
                        <Ident>
                            <Id>1234</Id>
                            <TypeId DN="Identifikator fra Helsetjenesteenhetsregisteret" S="1.16.578.1.12.4.3.1.8116" V="HER" />
                        </Ident>
                        <Ident>
                            <Id>1234567</Id>
                            <TypeId DN="HPR-nummer" S="1.16.578.1.12.3.1.1.8116" V="HPR" />
                        </Ident>
                        <Ident>
                            <Id>01010112377</Id>
                            <TypeId DN="Fødselsnummer" V="FNR" S="6.87.654.3.21.9.8.7.6543.2198" />
                        </Ident>
                        <Address>
                            <StreetAdr>Oppdiktet gate 9001</StreetAdr>
                            <PostalCode>9998</PostalCode>
                            <City>Oppdiktet</City>
                        </Address>
                        <TeleCom>
                            <TeleAddress V="98765432" />
                            <TypeTelecom DN="Hovedtelefon" />
                        </TeleCom>
                    </HealthcareProfessional>
                </Organisation>
            </Sender>
            <Receiver>
                <Organisation>
                    <OrganisationName>NAV</OrganisationName>
                    <Ident>
                        <Id>889640782</Id>
                        <TypeId DN="Organisasjonsnummeret i Enhetsregister (Brønnøysund)" S="2.16.578.1.12.4.1.1.9051" V="ENH" />
                    </Ident>
                    <Ident>
                        <Id>79768</Id>
                        <TypeId DN="Identifikator fra Helsetjenesteenhetsregisteret (HER-id)" S="2.16.578.1.12.4.1.1.9051" V="HER" />
                    </Ident>
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
                <IssueDate V="2019-01-16T21:57:36" />
                <MsgType DN="XML-instans" V="XML" />
                <Content>
                    <Dialogmelding xmlns="http://www.kith.no/xmlstds/dialog/2006-10-11"
                                   xmlns:xsd="http://www.w3.org/2001/XMLSchema">
                        <Notat>
                            <TemaKodet DN="Henvendelse om sykefraværsoppfølging" S="2.16.578.1.12.4.1.1.8128" V="1" />
                            <TekstNotatInnhold xsi:type="xsd:string">Hei,Det gjelder pas. Sender som vedlegg epikrisen</TekstNotatInnhold>
                            <DokIdNotat>A1578B81-0042-453B-8527-6CF182BDA6C7</DokIdNotat>
                            <DatoNotat>2019-01-16</DatoNotat>
                            <Foresporsel>
                                <TypeForesp DN="Påminnelse forespørsel om pasient" S="2.16.578.1.12.4.1.1.8129" V="2"/>
                                <Sporsmal>
                                    The Foresporsel tag including content is currently ignored if the dialogmelding is
                                    of type dialog_notat/henvendelse fra lege. This is necessary because EPJs were
                                    allowed to send incorrect XML to previous system EIA.
                                </Sporsmal>
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
