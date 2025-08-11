import xmlFormatter from "xml-formatter";

const xmlFormatterOptions = { indentation: "  ", lineSeparator: "\n" };

export const decodeMultipart = (multipart: string) => {
  const m = multipart.match(/^--([^\r\n]+)\r?\n/);
  if (!m) return multipart;
  const boundary = m[1].trim();

  const startRe = new RegExp(`^--${escapeRe(boundary)}\\r?\\n`);
  const endRe = new RegExp(`\\r?\\n--${escapeRe(boundary)}--\\s*$`);
  const core = multipart.replace(startRe, "").replace(endRe, "");

  const parts = core.split(new RegExp(`\\r?\\n--${escapeRe(boundary)}\\r?\\n`, "g"));

  const processed = parts.map((part) => {
    const split = part.split(/\r?\n\r?\n/);
    if (split.length < 2) return part.trim();

    const headersRaw = split.slice(0, -1).join("\n\n").trimEnd();
    const bodyRaw = split.slice(-1)[0];

    const headers = headersRaw
      .split(/\r?\n/)
      .map((l) => l.trim())
      .filter((l) => l.length > 0 && !/^Content-Length\s*:/i.test(l));

    const isXml = /Content-Type\s*:\s*(?:application|text)\/xml/i.test(headersRaw);
    const isBase64 = /Content-Transfer-Encoding\s*:\s*base64/i.test(headersRaw);

    let body = bodyRaw;
    if (isXml && isBase64) {
      const decoded = safeAtob(bodyRaw.replace(/\s+/g, ""));
      body = xmlFormatter(decoded, xmlFormatterOptions);
    } else if (isXml) {
      body = xmlFormatter(bodyRaw, xmlFormatterOptions);
    }

    return [headers.join("\r\n"), "", body.trim()].join("\r\n");
  });

  return `--${boundary}\r\n${processed.join(`\r\n--${boundary}\r\n`)}\r\n--${boundary}--`;
};

const safeAtob = (s: string) =>
  typeof atob === "function" ? atob(s) : Buffer.from(s, "base64").toString("utf8");

const escapeRe = (s: string) => s.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
