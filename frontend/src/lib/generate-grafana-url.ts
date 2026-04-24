const GRAFANA_BASE_URL = process.env.NEXT_PUBLIC_GRAFANA_BASE_URL ?? "https://grafana.nav.cloud.nais.io/";
const GRAFANA_ORG_ID = process.env.NEXT_PUBLIC_GRAFANA_ORG_ID ?? "1";
const GRAFANA_LOKI_DATASOURCE_UID = process.env.NEXT_PUBLIC_GRAFANA_LOKI_DATASOURCE_UID ?? "";
const GRAFANA_CLUSTER = process.env.NEXT_PUBLIC_GRAFANA_CLUSTER ?? "dev-fss";

function buildLokiQuery(conversationId: string) {
  return `{cluster="${GRAFANA_CLUSTER}"} |= "${conversationId}"`;
}

export function generateGrafanaURLFromConversationId(conversationId: string) {
  if (!conversationId) {
    return `${GRAFANA_BASE_URL}/explore`;
  }

  if (!GRAFANA_LOKI_DATASOURCE_UID) {
    return `${GRAFANA_BASE_URL}/explore?orgId=${encodeURIComponent(GRAFANA_ORG_ID)}`;
  }

  const panes = {
    A: {
      datasource: GRAFANA_LOKI_DATASOURCE_UID,
      queries: [
        {
          refId: "A",
          datasource: {
            uid: GRAFANA_LOKI_DATASOURCE_UID,
            type: "loki",
          },
          expr: buildLokiQuery(conversationId),
          queryType: "range",
        },
      ],
      range: {
        from: "now-15h",
        to: "now",
      },
    },
  };

  const params = new URLSearchParams({
    orgId: GRAFANA_ORG_ID,
    schemaVersion: "1",
    panes: JSON.stringify(panes),
  });

  return `${GRAFANA_BASE_URL}/explore?${params.toString()}`;
}
