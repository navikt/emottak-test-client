import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

import { firaCode, inter, poppins } from "@/app/fonts";

export const metadata: Metadata = {
  title: "Emottak Test Client",
  description: "A Dev Tool for testing CPA integrations",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={`${inter.variable} ${firaCode.variable} ${poppins.variable}`}>
      <body className={inter.className}>{children}</body>
    </html>
  );
}
