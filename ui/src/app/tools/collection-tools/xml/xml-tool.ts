import {CollectionTool, ToolPart} from '../../../_interfaces/tool';

const XmlGrammarCreation: ToolPart = {id: 'grammar', name: 'Grammatik erstellen'};

const XmlDocumentCreation: ToolPart = {id: 'document', name: 'Dokument erstellen'};


export const XmlTool: CollectionTool = new class XmlToolClass extends CollectionTool {
  constructor() {
    super('xml', 'XML', [XmlGrammarCreation, XmlDocumentCreation], 'live');
  }
}();
