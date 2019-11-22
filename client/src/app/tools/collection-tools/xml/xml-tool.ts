import {CollectionTool, ExerciseTag} from '../../../_interfaces/tool';
import {XmlExerciseContent} from './xml-interfaces';

export const XmlTool: CollectionTool<XmlExerciseContent> = new (
  class XmlToolClass extends CollectionTool<XmlExerciseContent> {
    constructor() {
      super('xml', 'XML', [], 'live');
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
