import { frikortEgenandelForesporselRequest } from "@/ebxml/send-cpa/cpa-templates/cpa-requests/frikort-egenandelforesporsel-request";
import { EbxmlRequest } from "@/ebxml/send-cpa/types";
import {trekkopplysningInnmelding} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/trekkopplysning-innmelding";
import {sykmeldingRegistrering} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/sykmelding-registrering";
import {legemeldingLegeerklaring} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/legemelding-legeerklaring";
import {dialogmoteInnkallingMoteRespons} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/dialogmoteinnkalling-moterespons";
import {henvendelsefralegeHenvendelse} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/henvendelsefralege-henvendelse";
import {foresporselfrasaksbehandlerForesporselsvar} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/foresporselfrasaksbehandler-foresporselsvar";
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
  {
    type: "DialogmoteInnkalling",
    name: "DialogmoteInnkalling MoteRespons",
    value: dialogmoteInnkallingMoteRespons,
  },
  {
    type: "HenvendelseFraLege",
    name: "HenvendelseFraLege Henvendelse",
    value: henvendelsefralegeHenvendelse,
  },
  {
    type: "ForesporselFraSaksbehandler",
    name: "ForesporselFraSaksbehandler ForesporselSvar",
    value: foresporselfrasaksbehandlerForesporselsvar,
  },

];
