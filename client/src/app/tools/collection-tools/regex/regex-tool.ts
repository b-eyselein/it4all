import {CollectionTool, ExerciseTag} from '../../../_interfaces/tool';
import {RegexExerciseContent} from './regex-interfaces';

export const RegexTool: CollectionTool<RegexExerciseContent> = new (
  class RegexToolClass extends CollectionTool<RegexExerciseContent> {
    constructor() {
      super('regex', 'Reguläre Ausdrücke', [{id: 'regex', name: 'Regulären Ausdruck erstellen'}], 'live');
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
