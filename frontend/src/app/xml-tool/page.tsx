"use client";

import { useMemo, useState } from "react";
import CodeMirror from "@uiw/react-codemirror";
import { xml } from "@codemirror/lang-xml";
import xmlFormatter from "xml-formatter";
import { Button } from "@/components/ui/button";

export default function Page() {
  const [input, setInput] = useState("");
  const [output, setOutput] = useState("");
  const [error, setError] = useState<string | null>(null);

  const extensions = useMemo(() => [xml()], []);

  function formatXml() {
    setError(null);
    try {
      const pretty = xmlFormatter(input, {
        indentation: "  ",
        collapseContent: false,
        lineSeparator: "\n",
      });
      setOutput(pretty);
    } catch (e: unknown) {
      const message = e instanceof Error ? e.message : "Unknown error";
      setError(message);
    }
  }

  function minifyXml() {
    setError(null);
    try {
      const min = xmlFormatter(input, { collapseContent: true, indentation: "" })
        .replace(/\n+/g, "")
        .replace(/\s{2,}/g, " ");
      setOutput(min);
    } catch (e: unknown) {
      const message = e instanceof Error ? e.message : "Unknown error";
      setError(message);
    }
  }

  async function copyOutput() {
    try {
      await navigator.clipboard.writeText(output);
    } catch {}
  }

  function clearAll() {
    setInput("");
    setOutput("");
    setError(null);
  }

  return (
    <main className="mx-auto max-w-7xl px-4">
      <h1 className="text-xl font-semibold tracking-tight">XML Formatter</h1>

      <div className="flex flex-wrap items-center gap-2">
        <Button size="sm" onClick={formatXml}>
          Format
        </Button>
        <Button size="sm" variant="secondary" onClick={minifyXml}>
          Minify
        </Button>
        <Button size="sm" variant="outline" onClick={copyOutput}>
          Copy output
        </Button>
        <Button size="sm" variant="ghost" onClick={clearAll}>
          Clear
        </Button>
      </div>

      {error && (
        <p className="rounded-md border border-red-200 bg-red-50 p-2 text-sm text-red-700">
          {error}
        </p>
      )}

      <div className="grid gap-4 md:grid-cols-1">
        <div className="space-y-2">
          <p className="text-xl font-medium">Input</p>
          <CodeMirror
            value={input}
            height="22rem"
            extensions={extensions}
            basicSetup={{ lineNumbers: true }}
            onChange={setInput}
          />
        </div>

        <div className="space-y-2">
          <p className="text-xl font-medium">Output</p>
          <CodeMirror
            value={output}
            height="22rem"
            extensions={extensions}
            basicSetup={{ lineNumbers: true }}
            onChange={setOutput}
          />
        </div>
      </div>
    </main>
  );
}
