import {CollectionTool, ExerciseTag, ToolPart} from '../../../_interfaces/tool';
import {UmlExerciseContent} from './uml-interfaces';

const UmlClassSelectionPart: ToolPart = {name: 'Klassenselektion', id: 'classSelection'};

export const UmlTool: CollectionTool<UmlExerciseContent> = new (
  class UmlToolClass extends CollectionTool<UmlExerciseContent> {
    constructor() {
      super('uml', 'UML-Klassendiagramme', [UmlClassSelectionPart], 'beta');
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
