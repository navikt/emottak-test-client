const GRAFANA_BASE_URL = process.env.NEXT_PUBLIC_GRAFANA_BASE_URL ?? "https://grafana.nav.cloud.nais.io";
const GRAFANA_LOKI_DATASOURCE_UID =
  process.env.NEXT_PUBLIC_GRAFANA_LOKI_DATASOURCE_UID ?? "P7BE696147D279490";
const GRAFANA_SERVICE_NAME = process.env.NEXT_PUBLIC_GRAFANA_SERVICE_NAME ?? "";
const GRAFANA_CLUSTER = process.env.NEXT_PUBLIC_GRAFANA_CLUSTER ?? "dev-fss";
const GRAFANA_TIME_FROM = process.env.NEXT_PUBLIC_GRAFANA_TIME_FROM ?? "now-15m";
const GRAFANA_TIME_TO = process.env.NEXT_PUBLIC_GRAFANA_TIME_TO ?? "now";

function getGrafanaBaseUrl() {
  return GRAFANA_BASE_URL.replace(/\/+$/, "");
}

export function generateGrafanaURLFromConversationId(conversationId: string) {
  const grafanaBaseUrl = getGrafanaBaseUrl();
  const routeServiceName = GRAFANA_SERVICE_NAME || "all";
  const filters = GRAFANA_SERVICE_NAME
    ? `service_name|=|${GRAFANA_SERVICE_NAME}`
    : `k8s_cluster_name|=|${GRAFANA_CLUSTER}`;
  const params = new URLSearchParams({
    patterns: "[]",
    from: GRAFANA_TIME_FROM,
    to: GRAFANA_TIME_TO,
    "var-lineFormat": "",
    "var-ds": GRAFANA_LOKI_DATASOURCE_UID,
    "var-filters": filters,
    "var-fields": "",
    "var-levels": "",
    "var-metadata": "",
    "var-jsonFields": "",
    "var-patterns": "",
    "var-lineFilterV2": "",
    "var-lineFilters": conversationId
      ? `caseInsensitive,0|__gfp__=|${conversationId}`
      : "",
    timezone: "browser",
    "var-all-fields": "",
    userDisplayedFields: "false",
    displayedFields: "[]",
    urlColumns: "[]",
    visualizationType: '"logs"',
    prettifyLogMessage: "false",
    sortOrder: '"Descending"',
    wrapLogMessage: "false",
  });

  return `${grafanaBaseUrl}/a/grafana-lokiexplore-app/explore/service_name/${encodeURIComponent(routeServiceName)}/logs?${params.toString()}`;
}
