import { frikortEgenandelForesporselRequest } from "@/ebxml/send-cpa/cpa-templates/cpa-requests/frikort-egenandelforesporsel-request";
import { pasientlisteForesporselHentPasientlisteRequest } from "@/ebxml/send-cpa/cpa-templates/cpa-requests/pasientlisteforesporsel-hentpasientliste-request";
import { EbxmlRequest } from "@/ebxml/send-cpa/types";

export type CpaRequestTemplate = {
  type: string;
  name: string;
  value: EbxmlRequest;
};

export const cpaRequestTemplates: CpaRequestTemplate[] = [
  {
    type: "Frikort",
    name: "EgenandelForesporsel",
    value: frikortEgenandelForesporselRequest,
  },
  {
    type: "Pasientliste",
    name: "HentPasientliste",
    value: pasientlisteForesporselHentPasientlisteRequest,
  },
];
