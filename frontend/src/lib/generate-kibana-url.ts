import rison from "rison";

export function generateKibanaURL(messageId: string) {
  const baseURL = "https://logs.adeo.no/app/discover#/?";

  const _g = {
    filters: [],
    refreshInterval: { pause: true, value: 5000 },
    time: { from: "now-15m", to: "now" },
  };

  const _a = {
    columns: ["exception", "message"],
    dataSource: { dataViewId: "96e648c0-980a-11e9-830a-e17bbd64b4db", type: "dataView" },
    filters: [
      {
        $state: { store: "appState" },
        meta: {
          alias: null,
          disabled: false,
          field: "cluster",
          index: "96e648c0-980a-11e9-830a-e17bbd64b4db",
          key: "cluster",
          negate: false,
          params: { query: "dev-fss" },
          type: "phrase",
        },
        query: { match_phrase: { cluster: "dev-fss" } },
      },
      {
        $state: { store: "appState" },
        meta: {
          alias: null,
          disabled: false,
          index: "96e648c0-980a-11e9-830a-e17bbd64b4db",
          key: "x_messageId",
          negate: false,
          params: { query: messageId },
          type: "phrase",
        },
        query: { match_phrase: { x_messageId: messageId } },
      },
    ],
    grid: { columns: { exception: { width: 296 } } },
    hideChart: true,
    interval: "auto",
    query: { language: "kuery", query: "" },
    sort: [["@timestamp", "desc"]],
  };

  const encodedG = encodeURIComponent(rison.encode(_g));
  const encodedA = encodeURIComponent(rison.encode(_a));

  return `${baseURL}_g=${encodedG}&_a=${encodedA}`;
}

export function generateKibanaURLFromConversationId(conversationId: string) {
  const baseURL = "https://logs.adeo.no/app/discover#/?";

  const _g = {
    filters: [],
    refreshInterval: { pause: true, value: 5000 },
    time: { from: "now-15m", to: "now" },
  };

  const _a = {
    columns: ["exception", "message"],
    dataSource: { dataViewId: "96e648c0-980a-11e9-830a-e17bbd64b4db", type: "dataView" },
    filters: [
      {
        $state: { store: "appState" },
        meta: {
          alias: null,
          disabled: false,
          field: "cluster",
          index: "96e648c0-980a-11e9-830a-e17bbd64b4db",
          key: "cluster",
          negate: false,
          params: { query: "dev-fss" },
          type: "phrase",
        },
        query: { match_phrase: { cluster: "dev-fss" } },
      },
      {
        $state: { store: "appState" },
        meta: {
          alias: null,
          disabled: false,
          index: "96e648c0-980a-11e9-830a-e17bbd64b4db",
          key: "x_conversationId",
          negate: false,
          params: { query: conversationId },
          type: "phrase",
        },
        query: { match_phrase: { x_conversationId: conversationId } },
      },
    ],
    grid: { columns: { exception: { width: 296 } } },
    hideChart: true,
    interval: "auto",
    query: { language: "kuery", query: "" },
    sort: [["@timestamp", "desc"]],
  };

  const encodedG = encodeURIComponent(rison.encode(_g));
  const encodedA = encodeURIComponent(rison.encode(_a));

  return `${baseURL}_g=${encodedG}&_a=${encodedA}`;
}
