import { frikortEgenandelForesporselRequest } from "@/ebxml/send-cpa/cpa-templates/cpa-requests/frikort-egenandelforesporsel-request";
import { EbxmlRequest } from "@/ebxml/send-cpa/types";
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
    type: "Trekkopplysning",
    name: "Trekkopplysning Innmelding",
    value: trekkopplysningInnmelding,
  },
];
