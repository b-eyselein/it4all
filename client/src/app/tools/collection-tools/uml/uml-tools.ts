import {ExerciseTag, Tool} from '../../../_interfaces/tool';

export const UmlTool: Tool = new (
  class UmlToolClass extends Tool {
    constructor() {
      super('uml', 'UML-Klassendiagramme', [], 'beta');
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
