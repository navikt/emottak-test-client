import { encodeXmlToBase64 } from "@/lib/utils";
import { EbxmlRequest } from "../../types";
import { v4 as uuidv4 } from "uuid";

export const pasientlisteForesporselHentPasientlisteWithHelseIdRequest: EbxmlRequest = {
  conversationId: uuidv4().toString(),
  messageId: uuidv4().toString(),
  fromPartyId: "13579",
  fromRole: "Fastlege",
  toPartyId: "79768",
  toRole: "Fastlegeregister",
  cpaId: "nav:qass:36666",
  service: "PasientlisteForesporsel",
  action: "HentPasientliste",
  signPayload: true,
  ebxmlPayload: {
    base64Content: encodeXmlToBase64(
      `
<?xml version="1.0" encoding="utf-8"?>
<MsgHead xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xsi:schemaLocation="http://www.kith.no/xmlstds/msghead/2006-05-24 MsgHead-v1_2.xsd" xmlns="http://www.kith.no/xmlstds/msghead/2006-05-24">
  <MsgInfo>
    <Type V="PasientlisteForesporsel" DN="Pasientliste abonnement" />
    <MIGversion>v1.2 2006-05-24</MIGversion>
    <GenDate>2025-05-30T17:19:45+02:00</GenDate>
    <MsgId>fd36e9d0-0e7f-409d-94b4-73771c4c4d26</MsgId>
    <Ack V="J" DN="Ja" />
    <Sender>
      <Organisation>
        <OrganisationName>WebMed Test HelseNorge</OrganisationName>
        <Ident>
          <Id>8095699</Id>
          <TypeId V="HER" S="2.16.578.1.12.4.1.1.9051" DN="HER-id" />
        </Ident>
        <Ident>
          <Id>100112140</Id>
          <TypeId V="ENH" S="2.16.578.1.12.4.1.1.9051" DN="Organisasjonsnummeret i Enhetsregister" />
        </Ident>
        <Address>
          <StreetAdr>Søren Bulls vei 25</StreetAdr>
          <PostalCode>1051</PostalCode>
          <City>Oslo</City>
        </Address>
        <TeleCom>
          <TeleAddress V="tel:73521234" />
        </TeleCom>
        <TeleCom>
          <TeleAddress V="fax:73524322" />
        </TeleCom>
        <TeleCom>
          <TeleAddress V="mailto:katskin@noline.no" />
        </TeleCom>
        <HealthcareProfessional>
          <TypeHealthcareProfessional V="LE" DN="Lege" />
          <RoleToPatient V="6" S="2.16.578.1.12.4.1.1.9034" DN="Fastlege" />
          <FamilyName>Koman</FamilyName>
          <GivenName>Magnar (ddfl) Dev</GivenName>
          <Ident>
            <Id>9144889</Id>
            <TypeId V="HPR" S="2.16.578.1.12.4.1.1.8116" DN="HPR-nummer" />
          </Ident>
          <Ident>
            <Id>8133984</Id>
            <TypeId V="HER" S="2.16.578.1.12.4.1.1.8116" DN="HER-id" />
          </Ident>
        </HealthcareProfessional>
      </Organisation>
    </Sender>
    <Receiver>
      <Organisation>
        <OrganisationName>NAV</OrganisationName>
        <Ident>
          <Id>79768</Id>
          <TypeId V="HER" S="2.16.578.1.12.4.1.1.9051" DN="HER-id" />
        </Ident>
      </Organisation>
    </Receiver>
  </MsgInfo>
  <Document>
    <DocumentConnection V="H" DN="Hoveddokument" />
    <RefDoc>
      <MsgType V="XML" DN="XML-instans" />
      <Content>
        <PasientlisteForesporsel xmlns="http://www.kith.no/xmlstds/nav/pasientliste/2010-02-01">
          <HentPasientliste>
            <FnrLege>04056600324</FnrLege>
            <KommuneNr>0301</KommuneNr>
            <Format V="PI" />
          </HentPasientliste>
        </PasientlisteForesporsel>
      </Content>
    </RefDoc>
  </Document>
      <Document>
        <RefDoc>
            <MsgType DN="Vedlegg" V="A"/>
            <MimeType>application/jwt</MimeType>
            <Description>HelseID</Description>
            <Content>
                <Base64Container xmlns="http://www.kith.no/xmlstds/base64container">
                    ZXlKaGJHY2lPaUpTVXpJMU5pSXNJbXRwWkNJNklqYzROalkzUmprd1JFTXhNVUpHTURSQ1JEazBOamRFTVVZNU1USXdRelJCTkRNME1FSTBRMFlpTENKNE5YUWlPaUpsUjFwZmEwNTNVblozVXpsc1IyWlNMVkpKVFZOclRrRjBUVGdpTENKMGVYQWlPaUpoZEN0cWQzUWlmUS5leUpoZFdRaU9pSnVZWFk2YzJsbmJpMXRaWE56WVdkbElpd2lhWE56SWpvaWFIUjBjSE02THk5b1pXeHpaV2xrTFhOMGN5NTBaWE4wTG01b2JpNXVieUlzSW01aVppSTZNVGMwT0RZeE56ZzFOU3dpYVdGMElqb3hOelE0TmpFM09EVTFMQ0psZUhBaU9qRTNORGcyTVRnME5UVXNJbUYxZEdoZmRHbHRaU0k2TVRjME9EWXhOemcxTkN3aWMyTnZjR1VpT2xzaWIzQmxibWxrSWl3aWNISnZabWxzWlNJc0luSmxZV1FpTENKdVlYWTZjMmxuYmkxdFpYTnpZV2RsTDIxeloyaGxZV1FpWFN3aWFXUndJam9pYVdSd2IzSjBaVzR0YjJsa1l5SXNJbWhsYkhObGFXUTZMeTlqYkdGcGJYTXZhV1JsYm5ScGRIa3ZjR2xrSWpvaU1EUXdOVFkyTURBek1qUWlMQ0pvWld4elpXbGtPaTh2WTJ4aGFXMXpMMmxrWlc1MGFYUjVMM0JwWkY5d2MyVjFaRzl1ZVcwaU9pSlFSM3BXZW5aUU1rcDJiRmhXS3l0UFNsTktRVkZITldRNU9VSklPRkZ6YVd0dGVIQmtTVUZMVTFwclBTSXNJbWhsYkhObGFXUTZMeTlqYkdGcGJYTXZhV1JsYm5ScGRIa3ZjMlZqZFhKcGRIbGZiR1YyWld3aU9pSTBJaXdpYUdWc2MyVnBaRG92TDJOc1lXbHRjeTlwWkdWdWRHbDBlUzloYzNOMWNtRnVZMlZmYkdWMlpXd2lPaUpvYVdkb0lpd2lhR1ZzYzJWcFpEb3ZMMk5zWVdsdGN5OXBaR1Z1ZEdsMGVTOXVaWFIzYjNKcklqb2lhVzUwWlhKdVpYUjBJaXdpYzNWaUlqb2lVRWQ2Vm5wMlVESktkbXhZVmlzclQwcFRTa0ZSUnpWa09UbENTRGhSYzJscmJYaHdaRWxCUzFOYWF6MGlMQ0p6YVdRaU9pSXdPVGN3UmpCRlJEWXdRelUxTWpVNU4wSkdRekkxTkRFMU1FWkJOREEyUkNJc0ltNWhiV1VpT2lKTFZrRlNWQ0JIVWtWV1RFbE9SeUlzSW1aaGJXbHNlVjl1WVcxbElqb2lSMUpGVmt4SlRrY2lMQ0puYVhabGJsOXVZVzFsSWpvaVMxWkJVbFFpTENKb1pXeHpaV2xrT2k4dlkyeGhhVzF6TDJod2NpOW9jSEpmYm5WdFltVnlJam9pTlRZMU5UQTFPVE16SWl3aVlXMXlJanBiSW5CM1pDSmRmUS5lcFkxZTRJb2FxVlRHbHpiZU5nU2trTC1uaGxaRDVMV19UdHZPTG1HTHR1ZFJIcHhNVkhrTGhRNXh5MlJIZGF3YzZtaFVyUFROellKUU10R3BBd283ZHRmTnlhT1daZk9EWncwc3VGZzNZSUJSZDI0STB3U3B1eFVpWnRsYUl0WUlNM3FSbWJuVUtNaHdHMHJyenZzd3dhOVI1YnA1aGhmR1FYY3RjUjNwX3lZdnlnVUVjUFh1bmVlNE02LXF2WnhBVUwxa000cjY1cUFnSlJJMWRFWUFFLUJTcW8td19WLW4za2JsY180bmFaa3NsQ2JOam9tcUEwSTV6YWlkUnY0YlFyd1R0RE90bmtwYWhSYmhra0s3c2l6bldQb1NaZFRvQjRqVV9RbnlFemJWbmFXVjlmM2hIaU1hOHRjYjRSMXplemRIUDhDY0RhSnMzYzduNjlsQjZhaGpsVTVaS0UxRFp3blAtOFdPVzBxS01YLXRueVNfWXpJN1FnZFdLRm5yNmc0S2VidVVVcmtwWnJ5MXNtVlRpTWRiY1FpaHB1OFBDbEp1WFJER1g0TFEzSEpBWlV5NllVR2pzbWtBQWd5cFZBdTNwbWV2UFdQc0hZSzdnb1doeFU5OEk3QW9KWnR1c3ctR3BCQ3hab09VV1VwemszZS13TFlPaHJ5Zzh6Nw==
                </Base64Container>
            </Content>
        </RefDoc>
    </Document>
</MsgHead>
      `.trim()
    ),
  },
};
