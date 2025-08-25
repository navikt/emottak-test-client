"use client";

import { TooltipProvider } from "@/components/ui/tooltip";

export function Providers({ children }: { children: React.ReactNode }) {
  return (
    <TooltipProvider delayDuration={150} disableHoverableContent={false}>
      {children}
    </TooltipProvider>
  );
}
