import { encodeXmlToBase64 } from "@/lib/utils";
import { EbxmlRequest } from "../../types";
import { v4 as uuidv4 } from "uuid";

export const trekkopplysningInnmelding: EbxmlRequest = {
  conversationId: uuidv4().toString(),
  messageId: uuidv4().toString(),
  fromPartyId: "13579",
  fromRole: "Fordringshaver",
  toPartyId: "79768",
  toRole: "Ytelsesutbetaler",
  cpaId: "nav:qass:36666",
  service: "Trekkopplysning",
  action: "Innmelding",
  signPayload: true,
  encryptPayload: true,
  useNewEmottakFlow: true,
  sendAsync: true,
  ebxmlPayload: {
    base64Content: encodeXmlToBase64(
      `
<?xml version="1.0" encoding="utf-8"?>
<MsgHead
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        xmlns="http://www.kith.no/xmlstds/msghead/2006-05-24">
    <MsgInfo>
        <Type V="INNRAPPORTERING_TREKK" DN="Innrapportering av trekk til NAV"/>
        <MIGversion>v1.2 2006-05-24</MIGversion>
        <GenDate>2026-06-11T14:17:48Z</GenDate>
        <MsgId>59ec1275-49ca-4d68-b29d-9b25f6f445de</MsgId>
        <Ack V="N" DN="Nei"/>
        <Sender>
            <Organisation>
                <OrganisationName>AIDN AS</OrganisationName>
                <Ident>
                    <Id>8139944</Id>
                    <TypeId V="HER" S="2.16.578.1.12.4.1.1.9051" DN="HER-id"/>
                </Ident>
                <Organisation>
                    <OrganisationName>Økonomi og oppgjør</OrganisationName>
                    <Ident>
                        <Id>8142626</Id>
                        <TypeId V="HER" S="2.16.578.1.12.4.1.1.9051" DN="HER-id"/>
                    </Ident>
                </Organisation>
            </Organisation>
        </Sender>
        <Receiver>
            <Organisation>
                <OrganisationName>NAV IKT</OrganisationName>
                <Ident>
                    <Id>79768</Id>
                    <TypeId V="HER" S="2.16.578.1.12.4.1.1.9051" DN="HER-id"/>
                </Ident>
            </Organisation>
        </Receiver>
        <Patient>
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
        <ContentDescription>SV:Innrapportering av trekk til NAV</ContentDescription>
        <RefDoc>
            <IssueDate V="2026-06-11T14:17:48Z"/>
            <MsgType V="XML" DN="XML-instans"/>
            <Content>
                <InnrapporteringTrekk xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                      xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                                      xmlns="http://www.kith.no/xmlstds/nav/innrapporteringtrekk/2010-02-04"
                                      xsi:schemaLocation="http://www.kith.no/xmlstds/nav/innrapporteringtrekk/2010-02-04 InnrapporteringTrekk-2010-02-04.xsd">
                    <Aksjonskode V="ENDR" DN="Endring av trekkvedtak"/>
                    <Identifisering>
                        <NavTrekkId>0013774979</NavTrekkId>
                        <DebitorId>
                            <Id xmlns="http://www.kith.no/xmlstds/felleskomponent1">03056537693</Id>
                            <TypeId V="FNR" S="2.16.578.1.12.4.1.1.8116" DN="Fødselsnummer" xmlns="http://www.kith.no/xmlstds/felleskomponent1"/>
                        </DebitorId>
                    </Identifisering>
                    <Trekk>
                        <KodeTrekktype V="VEDE" DN="Vederlagstrekk"/>
                        <KodeTrekkAlternativ V="LOPM" DN="Løpende trekk månedssats"/>
                        <Sats V="23242"/>
                    </Trekk>
                    <Periode>
                        <PeriodeFomDato>2026-07-01</PeriodeFomDato>
                    </Periode>
                    <Kreditor>
                        <TSSId>80000415406</TSSId>
                    </Kreditor>
                </InnrapporteringTrekk>
            </Content>
        </RefDoc>
    </Document>
</MsgHead>
    `.trim()
    ),
  },
};
