import { Skeleton } from "@/components/ui/skeleton";
import React from "react";

type Props = {};

const CodeMirrorSkeleton = (props: Props) => {
  return (
    <div className="">
      <Skeleton className="h-[600px] min-h-[600px] w-full rounded-md bg-gray-200 animate-pulse" />
    </div>
  );
};

export default CodeMirrorSkeleton;
