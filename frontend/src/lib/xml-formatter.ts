import xmlFormatter from "xml-formatter";

const xmlFormatterOptions = { indentation: "  ", lineSeparator: "\n" };
export const decodeResponse = (value: string) => {
  return value
    .split(/--------=/)
    .flatMap((chunk) => chunk.split(/\r?\n\r?\n/))
    .map((section) => {
      try {
        const line = section.trim();
        if (line.length === 0) return section;
        const decodedXml = atob(line);
        return xmlFormatter(decodedXml, xmlFormatterOptions);
      } catch {
        try {
          return xmlFormatter(section, xmlFormatterOptions);
        } catch {
          return section;
        }
      }
    })
    .filter((e) => e.length !== 0)
    .join("\n\n");
};
