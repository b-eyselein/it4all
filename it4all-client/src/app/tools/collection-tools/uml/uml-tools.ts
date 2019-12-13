import {CollectionTool, ToolPart} from '../../../_interfaces/tool';

export const UmlClassSelectionPart: ToolPart = {name: 'Klassenselektion', id: 'classSelection'};

export const UmlTool: CollectionTool = new class UmlToolClass extends CollectionTool {
  constructor() {
    super('uml', 'UML-Klassendiagramme', [UmlClassSelectionPart], 'beta');
  }
}();
