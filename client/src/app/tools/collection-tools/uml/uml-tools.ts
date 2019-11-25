import {CollectionTool, ExerciseTag, ToolPart} from '../../../_interfaces/tool';

const UmlClassSelectionPart: ToolPart = {name: 'Klassenselektion', id: 'classSelection'};

export const UmlTool: CollectionTool = new (
  class UmlToolClass extends CollectionTool {
    constructor() {
      super('uml', 'UML-Klassendiagramme', [UmlClassSelectionPart], 'beta');
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
