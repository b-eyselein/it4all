import {CollectionTool, ToolPart} from '../../../_interfaces/tool';

export const UmlClassSelectionPart: ToolPart = {name: 'Klassenselektion', id: 'classSelection'};

const UmlDiagramDrawingPart: ToolPart = {name: 'Diagramm zeichnen', id: 'diagramDrawing'};

export const UmlTool: CollectionTool = new class UmlToolClass extends CollectionTool {
  constructor() {
    super('uml', 'UML-Klassendiagramme', [UmlClassSelectionPart, UmlDiagramDrawingPart], 'beta');
  }
}();
