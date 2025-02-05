import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export const encodeXmlToBase64 = (xml: string): string => {
  return Buffer.from(xml, "utf-8").toString("base64");
};
