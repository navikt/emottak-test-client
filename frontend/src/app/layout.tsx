import type { Metadata } from "next";
import "./globals.css";

import { firaCode, inter, poppins } from "@/app/fonts";
import { Providers } from "@/app/providers";
import { SiteNavbar } from "@/components/navbar/navbar";

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
      <body className={inter.className}>
        <Providers>
          <SiteNavbar />
          {children}
        </Providers>
      </body>
    </html>
  );
}
