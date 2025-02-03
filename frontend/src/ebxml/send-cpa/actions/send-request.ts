"use server";

import { EbxmlRequest } from "@/ebxml/frikort/types";
import { config } from "@/infrastructure/config/config";

export type EbxmlResult =
  | { type: "Success"; message: string }
  | { type: "Failure"; error: string; statusCode?: number };

export async function sendEbxmlRequest(ebxmlRequest: EbxmlRequest): Promise<EbxmlResult> {
  const result = await fetch(`${config.testClientBackendBaseUrl}/ebxml/send-cpa`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(ebxmlRequest),
  });

  const responseBody = await result.json();

  if (result.ok) {
    if ("message" in responseBody) {
      return { type: "Success", message: responseBody.message };
    }
    throw new Error("Unexpected response structure for Success response");
  }

  const errorMessage = responseBody.error || "Unknown error";
  return { type: "Failure", error: errorMessage, statusCode: result.status };
}
