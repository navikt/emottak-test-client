"use client";

import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Alert } from "@/components/ui/alert";
import React, { useRef, useState } from "react";
import { Info, Loader2, RefreshCw } from "lucide-react";
import { EbxmlResult, sendEbxmlRequest } from "@/ebxml/send-cpa/actions/send-request";
import CodeMirrorWithDelay from "@/ebxml/send-cpa/components/CodeMirror/CodeMirrorWithDelay";
import CpaTemplateSelector from "@/ebxml/send-cpa/components/CpaTemplateSelector/CpaTemplateSelector";
import { frikortEgenandelForesporselRequest } from "@/ebxml/send-cpa/cpa-templates/cpa-requests/frikort-egenandelforesporsel-request";
import { Button } from "@/components/ui/button";
import CodeMirror from "@uiw/react-codemirror";
import { xml } from "@codemirror/lang-xml";
import { githubLight } from "@uiw/codemirror-theme-github";
import { generateKibanaURLFromConversationId } from "@/lib/generate-kibana-url";
import { v4 as uuidv4 } from "uuid";
import { Checkbox } from "@/components/ui/checkbox";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import { decodeMultipart } from "@/lib/xml-formatter";

export default function EbxmlForm() {
  const [formData, setFormData] = useState(frikortEgenandelForesporselRequest);
  const [response, setResponse] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [autoReloadConversationId, setAutoReloadConversationId] = useState(true);
  const [autoReloadMessageId, setAutoReloadMessageId] = useState(true);
  const [lastUsedConversationId, setLastUsedConversationId] = useState("");
  const [outboundXml, setOutboundXml] = useState<string | null>(null);

  const responseRef = useRef<HTMLDivElement | null>(null);

  const handleClear = () => {
    setFormData({
      conversationId: "",
      messageId: "",
      cpaId: "",
      fromPartyId: "",
      fromRole: "",
      toPartyId: "",
      toRole: "",
      service: "",
      action: "",
      signPayload: false,
      useNewEmottakFlow: true,
      ebxmlPayload: { base64Content: "" },
    });
    setError(null);
    setResponse(null);
    setOutboundXml(null);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handlePayloadChange = (value: string) => {
    setFormData((prev) => ({
      ...prev,
      ebxmlPayload: { base64Content: btoa(value) },
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setResponse(null);
    setError(null);
    setLoading(true);

    try {
      const result: EbxmlResult = await sendEbxmlRequest(formData);

      if (result.type === "Success") {
        setResponse(result.message);
      } else {
        setError(result.error);
      }

      setOutboundXml(result.outboundXml ?? null);
      setLastUsedConversationId(formData.conversationId);

      setFormData((prev) => ({
        ...prev,
        conversationId: autoReloadConversationId ? uuidv4().toString() : prev.conversationId,
        messageId: autoReloadMessageId ? uuidv4().toString() : prev.messageId,
      }));
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
      setTimeout(() => {
        responseRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
      }, 100);
    }
  };

  const kibanaLogUrl = generateKibanaURLFromConversationId(lastUsedConversationId);

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="flex justify-between items-center">
        <div className="flex items-center gap-4">
          <div>
            <Label>CPA Template:</Label>
            <CpaTemplateSelector selectedTemplate={formData} onTemplateChange={setFormData} />
          </div>

          <div className="flex items-center mt-6 gap-2">
            <Checkbox
              id="useNewEmottakFlow"
              checked={formData.useNewEmottakFlow}
              onCheckedChange={(checked) =>
                setFormData((prev) => ({ ...prev, useNewEmottakFlow: !!checked }))
              }
            />
            <Label htmlFor="useNewEmottakFlow" className="select-none cursor-pointer">
              Force new Emottak flow
            </Label>
            <Tooltip>
              <TooltipTrigger asChild>
                <button
                  type="button"
                  aria-label="What is the new Emottak flow?"
                  className="flex h-5 w-5 items-center justify-center rounded hover:bg-muted"
                >
                  <Info className="h-4 w-4 text-muted-foreground" />
                </button>
              </TooltipTrigger>
              <TooltipContent className="max-w-sm text-sm leading-relaxed">
                <div className="space-y-1">
                  <p className="font-medium">Routing override</p>
                  <p>
                    <b>Enabled</b>: Force the request through the <em>new Emottak flow</em>,
                    bypassing the normal router logic.
                  </p>
                  <p>
                    <b>Disabled</b>: Use the normal flow. We don’t send an override, and the
                    proxy/router decides the route.
                  </p>
                </div>
              </TooltipContent>
            </Tooltip>
          </div>
        </div>

        <Button type="button" variant="outline" className="bg-red-100" onClick={handleClear}>
          Clear Form
        </Button>
      </div>

      <div className="flex items-center gap-2">
        <div className="flex-1">
          <Label htmlFor="conversationId">Conversation ID</Label>
          <div className="flex items-center gap-2">
            <Input
              id="conversationId"
              name="conversationId"
              value={formData.conversationId}
              onChange={(e) => setFormData({ ...formData, conversationId: e.target.value })}
            />
            <Checkbox
              id="autoReloadConversationId"
              checked={autoReloadConversationId}
              onCheckedChange={(checked) => setAutoReloadConversationId(!!checked)}
            />
            <RefreshCw className="w-5 h-5 text-gray-500 cursor-pointer" />
          </div>
        </div>
      </div>

      <div className="flex items-center gap-2">
        <div className="flex-1">
          <Label htmlFor="messageId">Message ID</Label>
          <div className="h-full flex items-center gap-2">
            <Input
              id="messageId"
              name="messageId"
              value={formData.messageId}
              onChange={handleChange}
            />
            <Checkbox
              id="autoReloadMessageId"
              checked={autoReloadMessageId}
              onCheckedChange={(checked) => setAutoReloadMessageId(!!checked)}
            />
            <RefreshCw className="w-5 h-5 text-gray-500 cursor-pointer" />
          </div>
        </div>
      </div>

      <div>
        <Label htmlFor="cpaId">CPA ID</Label>
        <Input id="cpaId" name="cpaId" value={formData.cpaId} onChange={handleChange} />
      </div>
      <div>
        <Label htmlFor="fromPartyId">From Party ID</Label>
        <Input
          id="fromPartyId"
          name="fromPartyId"
          value={formData.fromPartyId}
          onChange={handleChange}
        />
      </div>
      <div>
        <Label htmlFor="fromRole">From Role</Label>
        <Input id="fromRole" name="fromRole" value={formData.fromRole} onChange={handleChange} />
      </div>
      <div>
        <Label htmlFor="toPartyId">To Party ID</Label>
        <Input id="toPartyId" name="toPartyId" value={formData.toPartyId} onChange={handleChange} />
      </div>
      <div>
        <Label htmlFor="toRole">Role</Label>
        <Input id="toRole" name="toRole" value={formData.toRole} onChange={handleChange} />
      </div>
      <div>
        <Label htmlFor="service">Service</Label>
        <Input id="service" name="service" value={formData.service} onChange={handleChange} />
      </div>
      <div>
        <Label htmlFor="action">Action</Label>
        <Input id="action" name="action" value={formData.action} onChange={handleChange} />
      </div>
      <div>
        <Label htmlFor="payload">Payload</Label>
        <CodeMirrorWithDelay
          value={atob(formData.ebxmlPayload?.base64Content || "")}
          onChange={(value) => handlePayloadChange(value)}
        />
      </div>
      <div className="flex items-center gap-4">
        <Button type="submit" disabled={loading} variant={"outline"} className="bg-green-100">
          {loading && <Loader2 className="animate-spin w-5 h-5" />}
          {loading ? "Sending..." : "Send Request"}
        </Button>

        <div className="flex items-center gap-2">
          <Checkbox
            id="signPayload"
            checked={formData.signPayload}
            onCheckedChange={(checked) =>
              setFormData((prev) => ({ ...prev, signPayload: !!checked }))
            }
          />
          <Label htmlFor="signPayload" className="select-none cursor-pointer">
            Sign payload
          </Label>
        </div>
      </div>

      <div ref={responseRef}>
        {response && (
          <div>
            <div className="rounded-md border border-border">
              <div className="bg-green-200 text-green-900 p-3 text-center text-lg font-semibold rounded-t-md">
                Success
              </div>
              <div className="px-4 py-2 border-t border-border text-sm text-muted-foreground">
                <a target="_blank" href={kibanaLogUrl} className="underline">
                  View Logs in Kibana
                </a>
              </div>

              <Accordion type="multiple" defaultValue={["decoded"]}>
                {outboundXml && (
                  <AccordionItem value="outbound">
                    <AccordionTrigger className="px-4 py-2 text-sm justify-center font-medium hover:bg-gray-200 bg-gray-100 h-full">
                      Request
                    </AccordionTrigger>
                    <AccordionContent className="px-0 py-0">
                      <CodeMirror
                        readOnly
                        value={decodeMultipart(outboundXml)}
                        extensions={[xml()]}
                        theme={githubLight}
                        className="rounded-md border"
                      />
                    </AccordionContent>
                  </AccordionItem>
                )}

                <AccordionItem value="decoded">
                  <AccordionTrigger className="px-4 py-2 text-sm justify-center font-medium hover:bg-gray-200 bg-gray-100 h-full">
                    Response
                  </AccordionTrigger>
                  <AccordionContent className="px-0 py-0">
                    <CodeMirror
                      readOnly
                      value={decodeMultipart(response)}
                      extensions={[xml()]}
                      theme={githubLight}
                      className="rounded-md border"
                    />
                  </AccordionContent>
                </AccordionItem>

                <AccordionItem value="raw">
                  <AccordionTrigger className="px-4 py-2 text-sm justify-center font-medium hover:bg-gray-200 bg-gray-100 h-full">
                    Raw Response
                  </AccordionTrigger>
                  <AccordionContent className="px-0 py-0">
                    <CodeMirror
                      readOnly
                      value={response}
                      extensions={[xml()]}
                      theme={githubLight}
                      className="rounded-md border"
                    />
                  </AccordionContent>
                </AccordionItem>
              </Accordion>
            </div>
          </div>
        )}

        {error && (
          <>
            <div className="space-y-4">
              <div className="bg-red-300 p-4 rounded-md  font-bold text-2xl flex justify-center">
                Error
              </div>
              <div>
                <a target="_blank" href={kibanaLogUrl}>
                  View Logs in Kibana
                </a>
              </div>
              <Alert variant="destructive">{error}</Alert>
            </div>
          </>
        )}
      </div>
    </form>
  );
}
