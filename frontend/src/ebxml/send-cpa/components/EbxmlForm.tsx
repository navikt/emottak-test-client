"use client";

import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Alert } from "@/components/ui/alert";
import React, { useRef, useState } from "react";
import { Loader2 } from "lucide-react";
import { EbxmlResult, sendEbxmlRequest } from "@/ebxml/send-cpa/actions/send-request";
import CodeMirrorWithDelay from "@/ebxml/send-cpa/components/CodeMirror/CodeMirrorWithDelay";
import CpaTemplateSelector from "@/ebxml/send-cpa/components/CpaTemplateSelector/CpaTemplateSelector";
import { frikortEgenandelForesporselRequest } from "@/ebxml/send-cpa/cpa-templates/cpa-requests/frikort-egenandelforesporsel-request";
import { Button } from "@/components/ui/button";
import CodeMirror from "@uiw/react-codemirror";
import { xml } from "@codemirror/lang-xml";
import { githubLight } from "@uiw/codemirror-theme-github";

export default function EbxmlForm() {
  const [formData, setFormData] = useState(frikortEgenandelForesporselRequest);
  const [response, setResponse] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const responseRef = useRef<HTMLDivElement | null>(null);

  const handleClear = () => {
    setFormData({
      cpaId: "",
      fromPartyId: "",
      fromRole: "",
      toPartyId: "",
      toRole: "",
      service: "",
      action: "",
      ebxmlPayload: { base64Content: "" },
    });
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
      const response: EbxmlResult = await sendEbxmlRequest(formData);

      if (response.type === "Success") {
        setResponse(response.message);
      } else {
        setError(response.error);
      }
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
      setTimeout(() => {
        responseRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
      }, 100);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="flex">
        <CpaTemplateSelector selectedTemplate={formData} onTemplateChange={setFormData} />
        <Button type="button" variant="ghost" onClick={handleClear}>
          Clear Form
        </Button>
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
      <Button type="submit" disabled={loading} variant={"outline"}>
        {loading && <Loader2 className="animate-spin w-5 h-5" />}
        {loading ? "Sending..." : "Send Request"}
      </Button>
      <div ref={responseRef}>
        {response && (
          <>
            <div className="space-y-4">
              <div className="bg-green-300 p-4 rounded-md  font-bold text-2xl flex justify-center">
                Success
              </div>
              <Alert variant="default">
                <CodeMirror value={response} extensions={[xml()]} theme={githubLight} />
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
              <Alert variant="destructive">{error}</Alert>
            </div>
          </>
        )}
      </div>
    </form>
  );
}
