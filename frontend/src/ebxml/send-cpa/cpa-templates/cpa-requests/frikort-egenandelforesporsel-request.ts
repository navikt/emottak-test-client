import { EbxmlRequest } from "../../types";
import { v4 as uuidv4 } from "uuid";

export const frikortEgenandelForesporselRequest: EbxmlRequest = {
  messageId: uuidv4().toString(),
  fromPartyId: "13579",
  fromRole: "Behandler",
  toPartyId: "79768",
  toRole: "Frikortregister",
  cpaId: "nav:qass:36666",
  service: "HarBorgerFrikort",
  action: "EgenandelForesporsel",
  ebxmlPayload: {
    base64Content: btoa(
      `
<?xml version="1.0" encoding="utf-8"?>
<ns:MsgHead xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:rawXsd="http://www.w3.org/2001/XMLSchema" xsi:schemaLocation="http://www.kith.no/xmlstds/msghead/2006-05-24 MsgHead-v1_2.rawXsd" xmlns:ns="http://www.kith.no/xmlstds/msghead/2006-05-24">
    <ns:MsgInfo>
        <ns:Type V="EgenandelForesporsel" DN="Forespørsel om egenandel"/>
        <ns:MIGversion>v1.2 2006-05-24</ns:MIGversion>
        <ns:GenDate>2024-03-13T16:08:43</ns:GenDate>
        <ns:MsgId>3b938c71-0ccf-4ace-86f1-7a2aaefa6f2b</ns:MsgId>
        <ns:Sender>
            <ns:Organisation>
                <ns:OrganisationName>Speare AS</ns:OrganisationName>
                <ns:Ident>
                    <ns:Id>8141253</ns:Id>
                    <ns:TypeId V="HER" DN="HER-id" S="2.16.578.1.12.4.1.1.9051"/>
                </ns:Ident>
                <ns:Ident>
                    <ns:Id>993954896</ns:Id>
                    <ns:TypeId V="ENH" DN="Organisasjonsnummeret i Enhetsregister" S="2.16.578.1.12.4.1.1.9051"/>
                </ns:Ident>
            </ns:Organisation>
        </ns:Sender>
        <ns:Receiver>
            <ns:Organisation>
                <ns:OrganisationName>NAV</ns:OrganisationName>
                <ns:Ident>
                    <ns:Id>79768</ns:Id>
                    <ns:TypeId V="HER" DN="HER-id" S="2.16.578.1.12.4.1.1.9051"/>
                </ns:Ident>
                <ns:Ident>
                    <ns:Id>889640782</ns:Id>
                    <ns:TypeId V="ENH" DN="Organisasjonsnummeret i Enhetsregister" S="2.16.578.1.12.4.1.1.9051"/>
                </ns:Ident>
            </ns:Organisation>
        </ns:Receiver>
    </ns:MsgInfo>
    <ns:Document>
        <ns:DocumentConnection V="H" DN="Hoveddokument"/>
        <ns:RefDoc>
            <ns:MsgType V="XML" DN="XML-instans"/>
            <ns:MimeType>text/rawXml</ns:MimeType>
            <ns:Description>EgenandelForesporsel</ns:Description>
            <ns:Content>
                <EgenandelForesporselV2 xmlns="http://www.kith.no/xmlstds/nav/egenandel/2016-06-10">
                    <HarBorgerFrikort>
                        <BorgerFnr>05058705065</BorgerFnr>
                        <Dato>2022-12-01</Dato>
                        <TjenestetypeKode>PS</TjenestetypeKode>
                    </HarBorgerFrikort>
                </EgenandelForesporselV2>
            </ns:Content>
        </ns:RefDoc>
    </ns:Document>
</ns:MsgHead>
    `.trim()
    ),
  },
};
