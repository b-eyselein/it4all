import {ExerciseTag, Tool} from '../../../_interfaces/tool';

export const RegexTool: Tool = new (
  class RegexToolClass extends Tool {
    constructor() {
      super('regex', 'Reguläre Ausdrücke', [{id: 'regex', name: 'Regulären Ausdruck erstellen'}]);
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
