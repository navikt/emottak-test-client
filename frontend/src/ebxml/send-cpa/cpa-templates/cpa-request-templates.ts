import { frikortEgenandelForesporselRequest } from "@/ebxml/send-cpa/cpa-templates/cpa-requests/frikort-egenandelforesporsel-request";
import { pasientlisteForesporselHentPasientlisteRequest } from "@/ebxml/send-cpa/cpa-templates/cpa-requests/pasientlisteforesporsel-hentpasientliste-request";
import { EbxmlRequest } from "@/ebxml/send-cpa/types";
import {
  pasientlisteForesporselHentPasientlisteWithHelseIdRequest
} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/pasientlisteforesporsel-hentpasientliste-request-with-helseid";
import {trekkopplysningInnmelding} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/trekkopplysning-innmelding";

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
  {
    type: "Pasientliste",
    name: "HentPasientliste med HelseID",
    value: pasientlisteForesporselHentPasientlisteWithHelseIdRequest,
  },
  {
    type: "Trekkopplysning",
    name: "Trekkopplysning Innmelding",
    value: trekkopplysningInnmelding,
  },
];
