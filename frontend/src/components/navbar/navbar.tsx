import Link from "next/link";
import { buttonVariants } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export function SiteNavbar() {
  return (
    <div className="border-b border-gray-200 h-10">
      <nav className="mx-auto flex max-w-7xl items-center gap-4 px-4">
        <Link href="/" className="text-base font-semibold tracking-tight">
          Emottak Test Client
        </Link>

        <Link
          href="/"
          className={cn(buttonVariants({ variant: "ghost", size: "sm" }), "rounded-lg")}
        >
          Home
        </Link>

        <Link
          href="/xml-tool"
          className={cn(buttonVariants({ variant: "ghost", size: "sm" }), "rounded-lg")}
        >
          XML Tool
        </Link>
        <Link
          href="/base64-tool"
          className={cn(buttonVariants({ variant: "ghost", size: "sm" }), "rounded-lg")}
        >
          Base64 Tool
        </Link>
      </nav>
    </div>
  );
}
