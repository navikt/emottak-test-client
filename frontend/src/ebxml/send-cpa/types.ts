export interface EbxmlRequest {
  conversationId: string;
  messageId: string;
  fromPartyId: string;
  fromRole: string;
  toPartyId: string;
  toRole: string;
  cpaId: string;
  service: string;
  action: string;
  ebxmlPayload?: EbxmlPayload;
}

export interface EbxmlPayload {
  base64Content: string;
  contentId?: string;
}
