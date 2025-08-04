"use client";

import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Alert} from "@/components/ui/alert";
import React, {useRef, useState} from "react";
import {Loader2, RefreshCw} from "lucide-react";
import {EbxmlResult, sendEbxmlRequest} from "@/ebxml/send-cpa/actions/send-request";
import CodeMirrorWithDelay from "@/ebxml/send-cpa/components/CodeMirror/CodeMirrorWithDelay";
import CpaTemplateSelector from "@/ebxml/send-cpa/components/CpaTemplateSelector/CpaTemplateSelector";
import {
  frikortEgenandelForesporselRequest
} from "@/ebxml/send-cpa/cpa-templates/cpa-requests/frikort-egenandelforesporsel-request";
import {Button} from "@/components/ui/button";
import CodeMirror from "@uiw/react-codemirror";
import {xml} from "@codemirror/lang-xml";
import {githubLight} from "@uiw/codemirror-theme-github";
import {generateKibanaURLFromConversationId} from "@/lib/generate-kibana-url";
import {v4 as uuidv4} from "uuid";
import {Checkbox} from "@/components/ui/checkbox";

export default function EbxmlForm() {
  const [formData, setFormData] = useState(frikortEgenandelForesporselRequest);
  const [response, setResponse] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [autoReloadConversationId, setAutoReloadConversationId] = useState(true);
  const [autoReloadMessageId, setAutoReloadMessageId] = useState(true);
  const [lastUsedConversationId, setLastUsedConversationId] = useState("");

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
      ebxmlPayload: {base64Content: ""},
      signPayload: false
    });
    setError(null);
    setResponse(null);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const {name, value} = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handlePayloadChange = (value: string) => {
    setFormData((prev) => ({
      ...prev,
      ebxmlPayload: {base64Content: btoa(value)},
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
        responseRef.current?.scrollIntoView({behavior: "smooth", block: "start"});
      }, 100);
    }
  };

  const logsLink = generateKibanaURLFromConversationId(lastUsedConversationId);

  const decodeResponse = (value: string) => {
    return value.split(/--------=/)
      .flatMap(c => c.split(/\r?\n\r?\n/))
      .map(c => {
        try {
          let line = c.trim()
          if (line.length === 0) return c
          const xml = atob(line)
          return prettifyXml(xml)
        } catch (t) {
          return c
        }
      }).filter(e => e.length !== 0)
      .join("\n\n")
  };

  const prettifyXml = function (sourceXml: string) {
    let xmlDoc = new DOMParser().parseFromString(sourceXml, 'application/xml');
    let xsltDoc = new DOMParser().parseFromString([
      // describes how we want to modify the XML - indent everything
      '<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">',
      '  <xsl:strip-space elements="*"/>',
      '  <xsl:template match="para[content-style][not(text())]">', // change to just text() to strip space in text nodes
      '    <xsl:value-of select="normalize-space(.)"/>',
      '  </xsl:template>',
      '  <xsl:template match="node()|@*">',
      '    <xsl:copy><xsl:apply-templates select="node()|@*"/></xsl:copy>',
      '  </xsl:template>',
      '  <xsl:output indent="yes"/>',
      '</xsl:stylesheet>',
    ].join('\n'), 'application/xml');

    let xsltProcessor = new XSLTProcessor();
    xsltProcessor.importStylesheet(xsltDoc);
    let resultDoc = xsltProcessor.transformToDocument(xmlDoc);
    let resultXml = new XMLSerializer().serializeToString(resultDoc);
    return resultXml;
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="flex justify-between">
        <div>
          <Label>CPA Template:</Label>
          <CpaTemplateSelector selectedTemplate={formData} onTemplateChange={setFormData}/>
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
              onChange={(e) => setFormData({...formData, conversationId: e.target.value})}
            />
            <Checkbox
              id="autoReloadConversationId"
              checked={autoReloadConversationId}
              onCheckedChange={(checked) => setAutoReloadConversationId(!!checked)}
            />
            <RefreshCw className="w-5 h-5 text-gray-500 cursor-pointer"/>
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
            <RefreshCw className="w-5 h-5 text-gray-500 cursor-pointer"/>
          </div>
        </div>
      </div>

      <div>
        <Label htmlFor="cpaId">CPA ID</Label>
        <Input id="cpaId" name="cpaId" value={formData.cpaId} onChange={handleChange}/>
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
        <Input id="fromRole" name="fromRole" value={formData.fromRole} onChange={handleChange}/>
      </div>
      <div>
        <Label htmlFor="toPartyId">To Party ID</Label>
        <Input id="toPartyId" name="toPartyId" value={formData.toPartyId} onChange={handleChange}/>
      </div>
      <div>
        <Label htmlFor="toRole">Role</Label>
        <Input id="toRole" name="toRole" value={formData.toRole} onChange={handleChange}/>
      </div>
      <div>
        <Label htmlFor="service">Service</Label>
        <Input id="service" name="service" value={formData.service} onChange={handleChange}/>
      </div>
      <div>
        <Label htmlFor="action">Action</Label>
        <Input id="action" name="action" value={formData.action} onChange={handleChange}/>
      </div>
      <div>
        <Label htmlFor="payload">Payload</Label>
        <CodeMirrorWithDelay
          value={atob(formData.ebxmlPayload?.base64Content || "")}
          onChange={(value) => handlePayloadChange(value)}
        />
      </div>
      <Button type="submit" disabled={loading} variant={"outline"} className="bg-green-100">
        {loading && <Loader2 className="animate-spin w-5 h-5"/>}
        {loading ? "Sending..." : "Send Request"}
      </Button>

      <div ref={responseRef}>
        {response && (
          <>
            <div className="space-y-4">
              <div className="bg-green-300 p-4 rounded-md  font-bold text-2xl flex justify-center">
                Success
              </div>
              <div>
                <a target="_blank" href={logsLink}>
                  View Logs in Kibana
                </a>
              </div>
              <Label>DECODED:</Label>
              <Alert variant="default">
                <CodeMirror readOnly value={decodeResponse(response)} extensions={[xml()]}
                            theme={githubLight}/>
              </Alert>
              RAW
              <Alert variant="default">
                <CodeMirror readOnly value={response} extensions={[xml()]} theme={githubLight}/>
              </Alert>
            </div>
          </>
        )}

        {error && (
          <>
            <div className="space-y-4">
              <div className="bg-red-300 p-4 rounded-md  font-bold text-2xl flex justify-center">
                Error
              </div>
              <div>
                <a target="_blank" href={logsLink}>
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
