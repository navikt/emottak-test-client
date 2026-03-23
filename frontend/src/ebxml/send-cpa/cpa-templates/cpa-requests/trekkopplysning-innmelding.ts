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
  useNewEmottakFlow: true,
  sendAsync: true,
  ebxmlPayload: {
    base64Content: encodeXmlToBase64(
      `
<?xml version="1.0" encoding="utf-8"?>
<ns:MsgHead xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:rawXsd="http://www.w3.org/2001/XMLSchema" xsi:schemaLocation="http://www.kith.no/xmlstds/msghead/2006-05-24 MsgHead-v1_2.rawXsd" xmlns:ns="http://www.kith.no/xmlstds/msghead/2006-05-24">
    <ns:MsgInfo>
        <ns:Type V="INNRAPPORTERING_TREKK" DN="Innrapportering av trekk til NAV"/>
        <ns:MIGversion>v1.2 2006-05-24</ns:MIGversion>
        <ns:GenDate>2026-03-23T08:00:00Z</ns:GenDate>
        <ns:MsgId>7f41c4e9-b6bd-44a3-822b-622332b4e421</ns:MsgId>
        <ns:Ack V="N" DN="Nei"/>
        <ns:Sender>
            <ns:Organisation>
                <ns:OrganisationName>AIDN AS</ns:OrganisationName>
                <ns:Ident>
                    <ns:Id>8139944</ns:Id>
                    <ns:TypeId V="HER" S="2.16.578.1.12.4.1.1.9051" DN="HER-id"/>
                </ns:Ident>
                <ns:Organisation>
                    <ns:OrganisationName>Økonomi og oppgjør</ns:OrganisationName>
                    <ns:Ident>
                        <ns:Id>8142626</ns:Id>
                        <ns:TypeId V="HER" S="2.16.578.1.12.4.1.1.9051" DN="HER-id"/>
                    </ns:Ident>
                </ns:Organisation>
            </ns:Organisation>
        </ns:Sender>
        <ns:Receiver>
            <ns:Organisation>
                <ns:OrganisationName>NAV IKT</ns:OrganisationName>
                <ns:Ident>
                    <ns:Id>79768</ns:Id>
                    <ns:TypeId V="HER" S="2.16.578.1.12.4.1.1.9051" DN="HER-id"/>
                </ns:Ident>
            </ns:Organisation>
        </ns:Receiver>
        <ns:Patient>
            <ns:FamilyName>HJELM</ns:FamilyName>
            <ns:GivenName>FASCINERENDE</ns:GivenName>
            <ns:Sex V="1" DN="Mann"/>
            <ns:Ident>
                <ns:Id>16894698199</ns:Id>
                <ns:TypeId V="FNR" S="2.16.578.1.12.4.1.1.8116" DN="Fødselsnummer"/>
            </ns:Ident>
        </ns:Patient>
    </ns:MsgInfo>
    <ns:Document>
        <ns:ContentDescription>SV:Innrapportering av trekk til NAV</ns:ContentDescription>
        <ns:RefDoc>
            <ns:IssueDate V="2026-03-23T08:00:00Z"/>
            <ns:MsgType V="XML" DN="XML-instans"/>
            <ns:Content>
                <InnrapporteringTrekk xmlns="http://www.kith.no/xmlstds/nav/innrapporteringtrekk/2010-02-04">
                    <Aksjonskode DN="Nytt trekk" V="NY"/>
                    <Identifisering>
                        <KreditorTrekkId>ee387905-4b77-47c2-9fba-e21847525c3e</KreditorTrekkId>
                        <DebitorId>
                            <Id>03056537693</Id>
                            <TypeId DN="Fødselsnummer" S="2.16.578.1.12.4.1.1.8116" V="FNR"/>
                        </DebitorId>
                    </Identifisering>
                    <Trekk>
                        <KodeTrekktype DN="Vederlagstrekk" V="VEDE"/>
                        <KodeTrekkAlternativ DN="Løpende trekk månedssats" V="LOPM"/>
                        <Sats V="21573"/>
                    </Trekk>
                    <Periode><PeriodeFomDato>2026-04-01</PeriodeFomDato></Periode>
                    <Kreditor><TSSId>80000415406</TSSId></Kreditor>
                </InnrapporteringTrekk>
            </ns:Content>
        </ns:RefDoc>
    </ns:Document>
</ns:MsgHead>
    `.trim()
    ),
  },
};
