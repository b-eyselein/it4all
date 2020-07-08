import {ToolPart} from '../../../_interfaces/tool';

export const XmlGrammarCreation: ToolPart = {id: 'grammar', name: 'Grammatik erstellen'};

export const XmlDocumentCreation: ToolPart = {id: 'document', name: 'Dokument erstellen'};


export function getXmlGrammarContent(rootNode: string): string {
  return `<!ELEMENT ${rootNode} (EMPTY)>`;
}

export function getXmlDocumentContent(rootNode: string): string {
  return `
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE ${rootNode} SYSTEM "${rootNode}.dtd">
<${rootNode}>
</${rootNode}>`.trim();
}

