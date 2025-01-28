"use client";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Alert } from "@/components/ui/alert";
import { EbxmlResult, sendEbxmlRequest } from "@/ebxml/frikort/actions/send-request";
import { EbxmlRequest } from "@/ebxml/frikort/types";
import React, { useState } from "react";
import CodeMirror from "@uiw/react-codemirror";
import { xml } from "@codemirror/lang-xml";

export default function EbxmlForm({ defaultValues }: { defaultValues: EbxmlRequest }) {
  const [formData, setFormData] = useState(defaultValues);
  const [response, setResponse] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

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

    try {
      const response: EbxmlResult = await sendEbxmlRequest(formData);

      if (response.type === "Success") {
        setResponse(response.message);
      } else {
        setError(response.error);
      }
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
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
        <Label htmlFor="toRole">To Role</Label>
        <Input id="toRole" name="toRole" value={formData.toRole} onChange={handleChange} />
      </div>
      <div>
        <Label htmlFor="cpaId">CPA ID</Label>
        <Input id="cpaId" name="cpaId" value={formData.cpaId} onChange={handleChange} />
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
        <CodeMirror
          value={atob(formData.ebxmlPayload?.base64Content || "")}
          height="600px"
          extensions={[xml()]}
          onChange={(value) => handlePayloadChange(value)}
          theme="light"
          className="border border-gray-300 rounded-md"
        />
      </div>
      <Button type="submit">Send Request</Button>
      {response && (
        <>
          <div className="space-y-4">
            <div className="bg-green-300 p-4 rounded-md  font-bold text-2xl flex justify-center">
              Success
            </div>
            <Alert variant="default">{response}</Alert>
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
    </form>
  );
}
