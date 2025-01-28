import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import EbxmlForm from "@/ebxml/frikort/components/SendFrikortView";
import { defaultEbxmlFrikortRequest } from "@/ebxml/frikort/default-request";

export default function Home() {
  return (
    <div className="max-w-5xl mx-auto mt-8">
      <Card>
        <CardHeader>
          <CardTitle>Send CPA Request</CardTitle>
        </CardHeader>
        <CardContent>
          <EbxmlForm defaultValues={defaultEbxmlFrikortRequest} />
        </CardContent>
      </Card>
    </div>
  );
}
