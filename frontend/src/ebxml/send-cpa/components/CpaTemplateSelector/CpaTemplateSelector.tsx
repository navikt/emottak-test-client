import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  CpaRequestTemplate,
  cpaRequestTemplates,
} from "@/ebxml/send-cpa/cpa-templates/cpa-request-templates";
import { EbxmlRequest } from "@/ebxml/send-cpa/types";
import React from "react";

type Props = {
  selectedTemplate: EbxmlRequest;
  onTemplateChange: (template: EbxmlRequest) => void;
};

const CpaTemplateSelector = ({ selectedTemplate, onTemplateChange }: Props) => {
  const cpaTemplates = cpaRequestTemplates;

  const groupedTemplates = cpaTemplates.reduce<Record<string, CpaRequestTemplate[]>>(
    (groups, template) => {
      if (!groups[template.type]) {
        groups[template.type] = [];
      }
      groups[template.type].push(template);
      return groups;
    },
    {}
  );

  const handleChange = (value: string) => {
    const template = cpaTemplates.find((t) => t.name === value);
    if (template) {
      onTemplateChange(template.value);
    }
  };

  const currentTemplateName = cpaTemplates.find((t) => t.value === selectedTemplate)?.name || "";

  return (
    <div>
      <Select value={currentTemplateName} onValueChange={handleChange}>
        <SelectTrigger className="w-[300px] bg-white">
          <SelectValue placeholder="Load CPA Template" />
        </SelectTrigger>
        <SelectContent className="bg-white">
          {Object.entries(groupedTemplates).map(([type, templates]) => (
            <SelectGroup key={type}>
              <SelectLabel>{type}</SelectLabel>
              {templates.map((template) => (
                <SelectItem key={template.name} value={template.name}>
                  {template.name}
                </SelectItem>
              ))}
            </SelectGroup>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
};

export default CpaTemplateSelector;
