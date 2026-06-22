import { frikortEgenandelForesporselRequest } from "@/ebxml/send-cpa/cpa-templates/cpa-requests/frikort-egenandelforesporsel-request";
import { EbxmlRequest } from "@/ebxml/send-cpa/types";
import {trekkopplysningInnmelding} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/trekkopplysning-innmelding";
import {sykmeldingRegistrering} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/sykmelding-registrering";
import {legemeldingLegeerklaring} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/legemelding-legeerklaring";
import {
    frikortMengdeEgenandelForesporselRequest
} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/frikortmengde-egenandelforesporsel-request";

export type CpaRequestTemplate = {
  type: string;
  name: string;
  value: EbxmlRequest;
};

export const cpaRequestTemplates: CpaRequestTemplate[] = [
  {
    type: "Frikort",
    name: "EgenandelForesporsel 1",
    value: frikortEgenandelForesporselRequest,
  },
  {
    type: "FrikortMengde",
    name: "EgenandelForesporsel flere",
    value: frikortMengdeEgenandelForesporselRequest,
  },
  {
    type: "Trekkopplysning",
    name: "Trekkopplysning Innmelding",
    value: trekkopplysningInnmelding,
  },
  {
    type: "Sykmelding",
    name: "Sykmelding Registrering",
    value: sykmeldingRegistrering,
  },
  {
    type: "Legemelding",
    name: "Legemelding Legeerklaring",
    value: legemeldingLegeerklaring,
  },
];
