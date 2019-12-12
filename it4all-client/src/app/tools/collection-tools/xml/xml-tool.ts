import {CollectionTool} from '../../../_interfaces/tool';

export const XmlTool: CollectionTool = new class XmlToolClass extends CollectionTool {
  constructor() {
    super('xml', 'XML', [], 'live');
  }
}();
