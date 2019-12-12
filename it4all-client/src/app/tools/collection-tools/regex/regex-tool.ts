import {CollectionTool, ExerciseTag, ToolPart} from '../../../_interfaces/tool';

export const RegexExercisePart: ToolPart = {id: 'regex', name: 'Regulären Ausdruck erstellen'};

export const RegexTool: CollectionTool = new (
  class RegexToolClass extends CollectionTool {
    constructor() {
      super('regex', 'Reguläre Ausdrücke', [RegexExercisePart], 'live');
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
