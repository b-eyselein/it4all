import {ExerciseTag, Tool, ToolPart} from '../../../_interfaces/tool';

const UmlClassSelectionPart: ToolPart = {name: 'Klassenselektion', id: 'classSelection'};

export const UmlTool: Tool = new (
  class UmlToolClass extends Tool {
    constructor() {
      super('uml', 'UML-Klassendiagramme', [UmlClassSelectionPart], 'beta', false, false, true);
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
