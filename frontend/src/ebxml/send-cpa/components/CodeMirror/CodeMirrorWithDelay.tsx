import React, { lazy, Suspense, useState, useEffect } from "react";
import CodeMirror from "@uiw/react-codemirror";
import { xml } from "@codemirror/lang-xml";
import { githubLight } from "@uiw/codemirror-theme-github";
import CodeMirrorSkeleton from "@/ebxml/send-cpa/components/CodeMirror/CodeMirrorSkeleton";

// UX: This silly thing is here because CodeMirror is recognized as loaded even though it's not,
// causing us to see a horizontal line instead of a loading skeleton that would normally indicate
// that something is loading.
const CodeMirrorWithDelay = ({
  value,
  onChange,
}: {
  value: string;
  onChange: (v: string) => void;
}) => {
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    const timeout = setTimeout(() => setLoaded(true), 10); // A hack to force showing skeleton during load
    return () => clearTimeout(timeout);
  }, []);

  if (!loaded) {
    return <CodeMirrorSkeleton />;
  }

  return (
    <div>
      <CodeMirror
        value={value}
        extensions={[xml()]}
        onChange={onChange}
        theme={githubLight}
        className="border border-gray-300 rounded-md"
      />
    </div>
  );
};

export default CodeMirrorWithDelay;
