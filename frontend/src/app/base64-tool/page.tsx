"use client";

import { useMemo, useState } from "react";
import CodeMirror from "@uiw/react-codemirror";
import { Button } from "@/components/ui/button";

type ValidationResult = { ok: true; value: string } | { ok: false; error: string };

export default function Page() {
  const [inputText, setInputText] = useState("");
  const [outputText, setOutputText] = useState("");
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isUrlSafeMode, setIsUrlSafeMode] = useState(false);

  const editorExtensions = useMemo(() => [], []);

  function stringToBase64(value: string, urlSafe: boolean): string {
    const bytes = new TextEncoder().encode(value);
    let binary = "";
    for (let i = 0; i < bytes.length; i++) binary += String.fromCharCode(bytes[i]);
    let encoded = btoa(binary);
    if (urlSafe) {
      encoded = encoded.replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/g, "");
    }
    return encoded;
  }

  function base64ToString(b64: string): string {
    const binary = atob(b64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
    const decoder = new TextDecoder("utf-8", { fatal: true });
    return decoder.decode(bytes);
  }

  function normalizeBase64Input(rawInput: string, urlMode: boolean): ValidationResult {
    let normalized = rawInput.trim().replace(/\s+/g, "");
    if (normalized.length === 0) return { ok: false, error: "Input is empty" };

    if (urlMode) normalized = normalized.replace(/-/g, "+").replace(/_/g, "/");

    const hasInvalidChar = /[^A-Za-z0-9+/=]/.test(normalized);
    if (hasInvalidChar) {
      return {
        ok: false,
        error: "Invalid character. Allowed characters are A-Z, a-z, 0-9, +, /, and =",
      };
    }

    if (!/^[A-Za-z0-9+/]*={0,2}$/.test(normalized)) {
      return { ok: false, error: "Padding must be at the end and can be at most two =" };
    }

    const firstPadIndex = normalized.indexOf("=");
    if (firstPadIndex !== -1 && /[^=]/.test(normalized.slice(firstPadIndex))) {
      return { ok: false, error: "Padding can only appear at the end" };
    }

    const withoutPadding = normalized.replace(/=+$/, "");
    if (withoutPadding.length % 4 === 1) {
      return {
        ok: false,
        error: "Invalid length. After removing padding, length must not be 1 modulo 4",
      };
    }

    const paddingNeeded = (4 - (withoutPadding.length % 4)) % 4;
    const padded = withoutPadding + "=".repeat(paddingNeeded);

    return { ok: true, value: padded };
  }

  function handleEncode() {
    setErrorMessage(null);
    try {
      const encoded = stringToBase64(inputText, isUrlSafeMode);
      setOutputText(encoded);
    } catch (e) {
      const message = e instanceof Error ? e.message : "Unknown error";
      setErrorMessage(message);
    }
  }

  function handleDecode() {
    setErrorMessage(null);
    const validation = normalizeBase64Input(inputText, isUrlSafeMode);
    if (!validation.ok) {
      setErrorMessage(validation.error);
      return;
    }
    try {
      const decoded = base64ToString(validation.value);
      setOutputText(decoded);
    } catch {
      setErrorMessage("Input is not valid Base64 or it is not valid UTF-8 text");
    }
  }

  async function handleCopy() {
    try {
      await navigator.clipboard.writeText(outputText);
    } catch {}
  }

  function handleClear() {
    setInputText("");
    setOutputText("");
    setErrorMessage(null);
  }

  return (
    <main className="mx-auto max-w-7xl px-4">
      <h1 className="text-xl font-semibold tracking-tight">Base64 Encoder and Decoder</h1>

      <div className="flex flex-wrap items-center gap-2">
        <Button size="sm" onClick={handleEncode}>
          Encode
        </Button>
        <Button size="sm" variant="secondary" onClick={handleDecode}>
          Decode
        </Button>
        <Button size="sm" variant="outline" onClick={handleCopy}>
          Copy output
        </Button>
        <Button size="sm" variant="ghost" onClick={handleClear}>
          Clear
        </Button>

        <label className="ml-4 inline-flex select-none items-center gap-2 text-sm">
          <input
            type="checkbox"
            className="h-4 w-4 accent-black"
            checked={isUrlSafeMode}
            onChange={(e) => setIsUrlSafeMode(e.target.checked)}
          />
          URL safe
        </label>
      </div>

      {errorMessage && (
        <p className="mt-2 rounded-md border border-red-200 bg-red-50 p-2 text-sm text-red-700">
          {errorMessage}
        </p>
      )}

      <div className="grid gap-4 md:grid-cols-1">
        <div className="space-y-2">
          <p className="text-xl font-medium">Input</p>
          <CodeMirror
            value={inputText}
            height="22rem"
            extensions={editorExtensions}
            basicSetup={{ lineNumbers: true }}
            onChange={setInputText}
          />
        </div>

        <div className="space-y-2">
          <p className="text-xl font-medium">Output</p>
          <CodeMirror
            value={outputText}
            height="22rem"
            extensions={editorExtensions}
            basicSetup={{ lineNumbers: true }}
            onChange={setOutputText}
          />
        </div>
      </div>
    </main>
  );
}
