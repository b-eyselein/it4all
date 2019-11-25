import {CollectionTool, ExerciseTag} from '../../../_interfaces/tool';

export const RegexTool: CollectionTool = new (
  class RegexToolClass extends CollectionTool {
    constructor() {
      super('regex', 'Reguläre Ausdrücke', [{id: 'regex', name: 'Regulären Ausdruck erstellen'}], 'live');
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
